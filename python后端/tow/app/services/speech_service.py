"""语音转文字服务 - 使用Vosk离线模型"""
import io
import os
import json
from typing import Optional
from pydub import AudioSegment
from vosk import Model, KaldiRecognizer

class SpeechService:
    """语音转文字服务"""
    
    def __init__(self):
        self.max_duration = 300  # 最大支持300秒(5分钟)
        self.model = None
        self._init_model()
    
    def _init_model(self):
        """初始化Vosk模型"""
        # 模型路径
        model_paths = [
            "f:\\temp\\tow\\vosk-model-small-cn-0.22",
            "f:\\temp\\tow\\models\\vosk-model-small-cn-0.22",
            "f:\\temp\\tow\\models\\vosk-model-cn-0.22",
        ]
        
        for path in model_paths:
            if os.path.exists(path):
                try:
                    self.model = Model(path)
                    print(f"Vosk模型加载成功: {path}")
                    return
                except Exception as e:
                    print(f"模型加载失败: {e}")
        
        print("警告: Vosk模型未找到，请下载模型:")
        print("百度网盘: https://pan.baidu.com/s/1FEH1xwDucdC3cEZSAyDOwQ?pwd=k8p5")
        print("提取码: k8p5")
        print("解压到: f:\\temp\\tow\\models\\vosk-model-small-cn-0.22")
    
    def convert_speech_to_text(self, audio_data: bytes, file_type: str = "wav") -> str:
        """
        将语音转换为文字
        
        Args:
            audio_data: 音频文件字节数据
            file_type: 音频文件类型 (wav, mp3, ogg, flac)
            
        Returns:
            识别出的文字内容
        """
        if self.model is None:
            raise ValueError("Vosk模型未加载，请先下载模型")
        
        try:
            # 将字节数据转换为 AudioSegment
            audio_segment = self._load_audio(audio_data, file_type)
            
            # 检查时长
            duration = len(audio_segment) / 1000  # 转换为秒
            if duration > self.max_duration:
                raise ValueError(f"音频时长超过限制，最大支持 {self.max_duration} 秒")
            
            # 转换为 WAV 格式（16kHz, 单声道, 16位）
            audio_segment = audio_segment.set_channels(1).set_frame_rate(16000).set_sample_width(2)
            
            # 获取PCM数据
            pcm_data = audio_segment.raw_data
            
            # 使用 Vosk 进行识别
            recognizer = KaldiRecognizer(self.model, 16000)
            recognizer.SetWords(True)
            
            # 分块处理音频
            chunk_size = 4000  # 每次处理4000字节
            result_text = ""
            
            for i in range(0, len(pcm_data), chunk_size):
                chunk = pcm_data[i:i + chunk_size]
                if recognizer.AcceptWaveform(chunk):
                    result = json.loads(recognizer.Result())
                    result_text += result.get("text", "")
            
            # 获取最后的partial结果
            final_result = json.loads(recognizer.FinalResult())
            result_text += final_result.get("text", "")
            
            if not result_text.strip():
                raise ValueError("无法识别语音内容")
            
            return result_text.strip()
            
        except Exception as e:
            raise ValueError(f"语音转文字失败: {str(e)}")
    
    def _load_audio(self, audio_data: bytes, file_type: str) -> AudioSegment:
        """加载音频文件"""
        audio_segment = None
        
        # 根据文件类型加载
        if file_type.lower() == "wav":
            audio_segment = AudioSegment.from_wav(io.BytesIO(audio_data))
        elif file_type.lower() == "mp3":
            audio_segment = AudioSegment.from_mp3(io.BytesIO(audio_data))
        elif file_type.lower() == "ogg":
            audio_segment = AudioSegment.from_ogg(io.BytesIO(audio_data))
        elif file_type.lower() == "flac":
            audio_segment = AudioSegment.from_flac(io.BytesIO(audio_data))
        else:
            raise ValueError(f"不支持的音频格式: {file_type}")
        
        return audio_segment


# 创建全局实例
speech_service = SpeechService()