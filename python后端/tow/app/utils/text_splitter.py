from langchain_text_splitters import RecursiveCharacterTextSplitter
from typing import List
import os


class TextSplitter:
    """文本切片工具"""
    
    def __init__(self, chunk_size: int = 500, chunk_overlap: int = 50):
        """
        初始化文本分割器
        
        Args:
            chunk_size: 每个块的大小(字符数)
            chunk_overlap: 块之间的重叠大小
        """
        self.splitter = RecursiveCharacterTextSplitter(
            chunk_size=chunk_size,
            chunk_overlap=chunk_overlap,
            length_function=len,
            separators=["\n\n", "\n", "。", "！", "？", "；", ",", ".", " ", ""]
        )
    
    def split_text(self, text: str) -> List[str]:
        """
        分割文本
        
        Args:
            text: 要分割的文本
            
        Returns:
            分割后的文本块列表
        """
        chunks = self.splitter.split_text(text)
        return chunks
    
    def split_file(self, file_path: str) -> List[str]:
        """
        读取并分割文件
        
        Args:
            file_path: 文件路径
            
        Returns:
            分割后的文本块列表
        """
        # 根据文件扩展名选择读取方式
        ext = os.path.splitext(file_path)[1].lower()
        
        if ext == '.txt' or ext == '.md':
            with open(file_path, 'r', encoding='utf-8') as f:
                text = f.read()
        elif ext == '.pdf':
            text = self._read_pdf(file_path)
        else:
            # 默认按文本处理
            with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                text = f.read()
        
        return self.split_text(text)
    
    def _read_pdf(self, file_path: str) -> str:
        """
        读取 PDF 文件内容
        
        Args:
            file_path: PDF文件路径
            
        Returns:
            提取的文本内容
        """
        try:
            # 尝试使用 PyPDF2
            import PyPDF2
            with open(file_path, 'rb') as f:
                reader = PyPDF2.PdfReader(f)
                text = ""
                for page in reader.pages:
                    text += page.extract_text() or ""
            return text
        except ImportError:
            try:
                # 尝试使用 pdfplumber
                import pdfplumber
                with pdfplumber.open(file_path) as pdf:
                    text = ""
                    for page in pdf.pages:
                        text += page.extract_text() or ""
                return text
            except ImportError:
                raise Exception("请安装 PyPDF2 或 pdfplumber 来处理 PDF 文件: pip install PyPDF2")
        except Exception as e:
            raise Exception(f"读取 PDF 文件失败: {e}")


# 创建全局实例
text_splitter = TextSplitter(chunk_size=500, chunk_overlap=50)
