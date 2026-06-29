"""
自动生成商家语料服务

根据商家的商品信息自动生成FAQ语料
"""

from sqlalchemy.orm import Session
from sqlalchemy import text
from app.models.mysql_models import get_db, TermCorpus
from app.services.merchant_db_service import merchant_db_service
from typing import List, Dict
import uuid
from datetime import datetime


class CorpusGeneratorService:
    """语料生成服务"""

    def __init__(self):
        pass

    def generate_corpus_from_products(self, merchant_id: int, db: Session) -> Dict:
        """
        根据商家商品自动生成语料

        Args:
            merchant_id: 商家ID
            db: 数据库会话

        Returns:
            生成结果统计
        """
        # 查询商家的商品
        products = db.execute(
            text("""
                SELECT id, product_name, price, stock, sales, brief, is_hot, is_new
                FROM term_products
                WHERE merchant_id = :merchant_id AND status = 1 AND deleted_at IS NULL
            """),
            {"merchant_id": merchant_id}
        ).fetchall()

        if not products:
            return {
                "success": False,
                "message": "该商家没有商品或商品已下架",
                "generated_count": 0
            }

        generated = []
        errors = []

        # 为每个商品生成语料
        for product in products:
            product_id, product_name, price, stock, sales, brief, is_hot, is_new = product

            try:
                # 1. 商品咨询类语料
                if brief:
                    corpus = self._create_corpus(
                        db, merchant_id,
                        f"{product_name}怎么样？",
                        f"{product_name}：{brief}",
                        f"{product_name},怎么样"
                    )
                    generated.append(corpus)

                    corpus = self._create_corpus(
                        db, merchant_id,
                        f"{product_name}好用吗？",
                        f"{product_name}：{brief}",
                        f"{product_name},好用"
                    )
                    generated.append(corpus)

                # 2. 价格咨询类语料
                corpus = self._create_corpus(
                    db, merchant_id,
                    f"{product_name}多少钱？",
                    f"{product_name}的价格是 ¥{float(price):.2f}元",
                    f"{product_name},价格,多少钱"
                )
                generated.append(corpus)

                corpus = self._create_corpus(
                    db, merchant_id,
                    f"{product_name}价格多少？",
                    f"{product_name}的价格是 ¥{float(price):.2f}元",
                    f"{product_name},价格"
                )
                generated.append(corpus)

                # 3. 库存咨询类语料
                if stock > 0:
                    corpus = self._create_corpus(
                        db, merchant_id,
                        f"{product_name}有货吗？",
                        f"{product_name}目前有现货，库存{stock}件，欢迎购买！",
                        f"{product_name},有货,库存"
                    )
                    generated.append(corpus)
                else:
                    corpus = self._create_corpus(
                        db, merchant_id,
                        f"{product_name}有货吗？",
                        f"抱歉，{product_name}目前缺货，您可以关注我们的到货通知。",
                        f"{product_name},有货"
                    )
                    generated.append(corpus)

                # 4. 销量咨询类语料
                if sales > 0:
                    corpus = self._create_corpus(
                        db, merchant_id,
                        f"{product_name}卖了多少？",
                        f"{product_name}已售出{sales}件，深受顾客好评！",
                        f"{product_name},销量,卖了多少"
                    )
                    generated.append(corpus)

                # 5. 规格咨询类语料
                corpus = self._create_corpus(
                    db, merchant_id,
                    f"{product_name}有什么规格？",
                    f"您好，{product_name}有多种规格可选，具体规格可在商品详情页查看。",
                    f"{product_name},规格"
                )
                generated.append(corpus)

            except Exception as e:
                errors.append({"product_id": product_id, "error": str(e)})

        # 6. 生成热销推荐语料
        hot_products = [p for p in products if p[6] == 1]  # is_hot
        if hot_products:
            hot_names = "、".join([p[1] for p in hot_products[:3]])
            corpus = self._create_corpus(
                db, merchant_id,
                "有什么热销的？",
                f"本店热销商品推荐：{hot_names}，这些都是顾客好评如潮的人气商品！",
                "热销,热卖,推荐",
                priority=5
            )
            generated.append(corpus)

        # 7. 生成新品推荐语料
        new_products = [p for p in products if p[7] == 1]  # is_new
        if new_products:
            new_names = "、".join([p[1] for p in new_products[:3]])
            corpus = self._create_corpus(
                db, merchant_id,
                "有什么新品？",
                f"本店新品上架：{new_names}，新鲜上市，优惠多多！",
                "新品,新到",
                priority=5
            )
            generated.append(corpus)

        # 8. 通用问候语料
        corpus = self._create_corpus(
            db, merchant_id,
            "你们店有什么？",
            f"本店主要销售：{'、'.join([p[1] for p in products[:5]])}等商品，欢迎选购！",
            "有什么,卖什么,商品"
        )
        generated.append(corpus)

        return {
            "success": True,
            "message": f"成功生成 {len(generated)} 条语料",
            "generated_count": len(generated),
            "product_count": len(products),
            "errors": errors
        }

    def _create_corpus(
        self,
        db: Session,
        merchant_id: int,
        question: str,
        answer: str,
        keywords: str,
        priority: int = 0
    ) -> Dict:
        """创建单条语料"""
        corpus = TermCorpus(
            id=str(uuid.uuid4()),
            merchant_id=merchant_id,
            question=question,
            answer=answer,
            keywords=keywords,
            priority=priority,
            enabled=1,
            created_at=datetime.now(),
            updated_at=datetime.now()
        )

        db.add(corpus)
        db.commit()
        db.refresh(corpus)

        return {
            "id": corpus.id,
            "question": corpus.question,
            "answer": corpus.answer
        }

    def clear_merchant_corpus(self, merchant_id: int, db: Session) -> int:
        """清空商家所有语料"""
        deleted = db.query(TermCorpus).filter(
            TermCorpus.merchant_id == merchant_id
        ).delete()
        db.commit()
        return deleted


# 创建全局实例
corpus_generator = CorpusGeneratorService()
