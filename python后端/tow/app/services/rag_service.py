from typing import List, Dict, Optional
from app.database.chroma_db import chroma_db
from app.utils.text_splitter import text_splitter
import os
import uuid
from datetime import datetime


class RAGService:
    """RAG 知识库服务"""
    
    def __init__(self):
        self.chroma_db = chroma_db
        self.text_splitter = text_splitter
    
    async def upload_knowledge(self, file_path: str, category: str = "general", 
                               metadata: Optional[Dict] = None) -> List[str]:
        """
        上传知识库文档
        
        Args:
            file_path: 文件路径
            category: 文档分类
            metadata: 额外元数据
            
        Returns:
            文档ID列表
        """
        try:
            # 读取并分割文件
            chunks = self.text_splitter.split_file(file_path)
            
            if not chunks:
                raise ValueError("文件内容为空或无法解析")
            
            # 准备元数据
            filename = os.path.basename(file_path)
            base_metadata = {
                "category": category,
                "filename": filename,
                "created_at": datetime.now().isoformat(),
                "chunk_count": len(chunks)
            }
            
            if metadata:
                base_metadata.update(metadata)
            
            # 为每个 chunk 创建元数据
            metadatas = []
            for i, chunk in enumerate(chunks):
                chunk_metadata = base_metadata.copy()
                chunk_metadata["chunk_index"] = i
                metadatas.append(chunk_metadata)
            
            # 生成文档ID
            doc_ids = [f"{uuid.uuid4()}" for _ in chunks]
            
            # 添加到向量数据库
            added_ids = self.chroma_db.add_documents(
                documents=chunks,
                metadatas=metadatas,
                ids=doc_ids
            )
            
            print(f"成功上传知识库文档: {filename}, 共 {len(chunks)} 个片段")
            return added_ids
            
        except Exception as e:
            print(f"上传知识库文档失败: {e}")
            raise
    
    def delete_knowledge(self, doc_ids: List[str]) -> bool:
        """
        删除知识库文档
        
        Args:
            doc_ids: 文档ID列表
            
        Returns:
            是否成功
        """
        try:
            success = self.chroma_db.delete_documents(doc_ids)
            if success:
                print(f"成功删除 {len(doc_ids)} 个文档")
            return success
        except Exception as e:
            print(f"删除知识库文档失败: {e}")
            return False
    
    def retrieve_context(self, question: str, top_k: int = 5) -> tuple[str, List[str]]:
        """
        检索相关上下文 (RAG 核心方法)
        
        Args:
            question: 用户问题
            top_k: 返回最相关的top_k条结果
            
        Returns:
            (上下文字符串, 来源文档ID列表)
        """
        try:
            # 从 Chroma 检索相似文档
            results = self.chroma_db.search_similar(question, top_k=top_k)
            
            if not results:
                return "", []
            
            # 拼接上下文
            context_parts = []
            source_ids = []
            
            for i, result in enumerate(results):
                doc_content = result['document']
                doc_id = result['id']
                metadata = result.get('metadata', {})
                
                # 格式化参考资料
                context_parts.append(
                    f"参考资料{i+1} (来源: {metadata.get('filename', 'unknown')}):\n{doc_content}"
                )
                source_ids.append(doc_id)
            
            context = "\n\n".join(context_parts)
            
            print(f"检索到 {len(results)} 条相关文档")
            return context, source_ids
            
        except Exception as e:
            print(f"检索上下文失败: {e}")
            return "", []
    
    def list_knowledge(self, limit: int = 100, offset: int = 0) -> List[Dict]:
        """
        列出知识库文档
        
        Args:
            limit: 返回数量限制
            offset: 偏移量
            
        Returns:
            文档列表
        """
        try:
            documents = self.chroma_db.list_documents(limit=limit, offset=offset)
            return documents
        except Exception as e:
            print(f"列出知识库文档失败: {e}")
            return []
    
    def get_document_count(self) -> int:
        """获取知识库文档总数"""
        return self.chroma_db.get_document_count()


# 创建全局实例
rag_service = RAGService()
