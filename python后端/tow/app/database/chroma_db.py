import chromadb
from chromadb.config import Settings as ChromaSettings
from typing import List, Dict, Optional
from langchain_huggingface import HuggingFaceEmbeddings
from app.config import settings
import uuid


class ChromaDBManager:
    """Chroma 向量数据库管理器"""
    
    def __init__(self):
        """初始化 Chroma 客户端"""
        # 初始化持久化客户端
        self.client = chromadb.PersistentClient(
            path=settings.CHROMA_PERSIST_DIR,
            settings=ChromaSettings(
                anonymized_telemetry=False
            )
        )
        
        # 获取或创建集合
        self.collection = self.client.get_or_create_collection(
            name="knowledge_base",
            metadata={"description": "智能客服知识库"}
        )
        
        # 初始化嵌入模型 (使用本地 HuggingFace 模型)
        # 注意: 首次使用会下载模型文件,约几百MB
        print("正在初始化 embedding 模型...")
        # 直接使用备用方案,避免网络问题
        print("使用简单的文本哈希作为备用方案 (生产环境建议配置真实的 embedding 模型)")
        self.embeddings = None
    
    def _generate_embedding(self, text: str) -> List[float]:
        """生成文本向量"""
        if self.embeddings:
            return self.embeddings.embed_query(text)
        else:
            # 备用方案: 使用简单哈希 (仅用于测试,生产环境应使用真正的 embedding 模型)
            import hashlib
            hash_value = hashlib.md5(text.encode()).hexdigest()
            # 转换为固定长度的向量 (384维,与 all-MiniLM-L6-v2 一致)
            return [float(ord(c)) / 255.0 for c in hash_value.ljust(384, '0')[:384]]
    
    def add_documents(self, documents: List[str], metadatas: List[Dict], ids: Optional[List[str]] = None) -> List[str]:
        """
        添加文档到向量库
        
        Args:
            documents: 文档内容列表
            metadatas: 元数据列表,每个元素对应一个文档
            ids: 文档ID列表,如果不提供则自动生成
            
        Returns:
            文档ID列表
        """
        if not ids:
            ids = [str(uuid.uuid4()) for _ in documents]
        
        # 生成向量
        embeddings = [self._generate_embedding(doc) for doc in documents]
        
        # 批量添加到 Chroma
        self.collection.add(
            documents=documents,
            metadatas=metadatas,
            ids=ids,
            embeddings=embeddings
        )
        
        return ids
    
    def delete_documents(self, ids: List[str]) -> bool:
        """
        删除指定文档
        
        Args:
            ids: 要删除的文档ID列表
            
        Returns:
            是否成功
        """
        try:
            self.collection.delete(ids=ids)
            return True
        except Exception as e:
            print(f"删除文档失败: {e}")
            return False
    
    def search_similar(self, query: str, top_k: int = 5) -> List[Dict]:
        """
        相似度检索
        
        Args:
            query: 查询文本
            top_k: 返回最相关的top_k条结果
            
        Returns:
            检索结果列表,每个元素包含 id, document, metadata, distance
        """
        # 生成查询向量
        query_embedding = self._generate_embedding(query)
        
        # 执行相似度搜索
        results = self.collection.query(
            query_embeddings=[query_embedding],
            n_results=top_k,
            include=["documents", "metadatas", "distances"]
        )
        
        # 格式化结果
        formatted_results = []
        if results['ids'] and results['ids'][0]:
            for i, doc_id in enumerate(results['ids'][0]):
                formatted_results.append({
                    'id': doc_id,
                    'document': results['documents'][0][i] if results['documents'] else '',
                    'metadata': results['metadatas'][0][i] if results['metadatas'] else {},
                    'distance': results['distances'][0][i] if results['distances'] else 0.0
                })
        
        return formatted_results
    
    def clear_collection(self) -> bool:
        """
        清空集合
        
        Returns:
            是否成功
        """
        try:
            # 删除并重新创建集合
            self.client.delete_collection(name="knowledge_base")
            self.collection = self.client.create_collection(
                name="knowledge_base",
                metadata={"description": "智能客服知识库"}
            )
            return True
        except Exception as e:
            print(f"清空集合失败: {e}")
            return False
    
    def get_document_count(self) -> int:
        """获取文档总数"""
        return self.collection.count()
    
    def list_documents(self, limit: int = 100, offset: int = 0) -> List[Dict]:
        """
        列出所有文档
        
        Args:
            limit: 返回数量限制
            offset: 偏移量
            
        Returns:
            文档列表
        """
        results = self.collection.get(
            limit=limit,
            offset=offset,
            include=["metadatas"]
        )
        
        documents = []
        if results['ids']:
            for i, doc_id in enumerate(results['ids']):
                documents.append({
                    'doc_id': doc_id,
                    'metadata': results['metadatas'][i] if results['metadatas'] else {}
                })
        
        return documents


# 创建全局单例
chroma_db = ChromaDBManager()
