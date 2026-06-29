import os
import asyncio
import numpy as np
import sys
import struct
import time
from typing import Optional, AsyncGenerator, Dict, List
from datetime import datetime, timedelta

# 添加 VoxCPM2 路径
sys.path.append(r"F:\AI\VoxCPM2")

try:
    from VoxCPM.src.voxcpm.core import VoxCPM
except ImportError:
    VoxCPM = None

# 配置
VOICE_OUTPUT_DIR = r"F:\temp\测试语言"
REFERENCE_VOICE_PATH = r"E:\aidown\AI配音\爻光.MP3"
SESSION_TIMEOUT_HOURS = 5

class TtsService:
    _instance = None
    _lock = asyncio.Lock()
    
    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
            cls._instance._voxcpm = None
            cls._instance._model_path = r"F:\AI\VoxCPM2\models\OpenBMB\VoxCPM2"
            cls._instance._sample_rate = 16000
            cls._instance._reference_voice_path = REFERENCE_VOICE_PATH
            cls._instance._output_dir = VOICE_OUTPUT_DIR
            # 会话语音缓存: {user_id: {conversation_id: {"files": [], "created_at": datetime}}}
            cls._instance._session_cache: Dict[str, Dict[str, dict]] = {}
        return cls._instance
    
    async def initialize(self):
        """异步初始化 TTS 模型"""
        if self._initialized:
            return True
        
        if VoxCPM is None:
            return False
        
        try:
            # 确保输出目录存在
            os.makedirs(self._output_dir, exist_ok=True)
            
            print(f"异步初始化 VoxCPM2...模型路径: {self._model_path}", flush=True)
            print(f"参考音色: {self._reference_voice_path}", flush=True)
            print(f"输出目录: {self._output_dir}", flush=True)
            
            # 在线程池中加载模型（避免阻塞事件循环）
            loop = asyncio.get_event_loop()
            self._voxcpm = await loop.run_in_executor(
                None,
                lambda: VoxCPM(
                    voxcpm_model_path=self._model_path,
                    zipenhancer_model_path=None,
                    enable_denoiser=False,
                    optimize=True
                )
            )
            self._initialized = True
            print("✅ VoxCPM2 异步初始化成功！", flush=True)
            return True
        except Exception as e:
            print(f"TTS初始化失败: {e}", flush=True)
            return False
    
    def _create_wav_header(self, data_size: int, sample_rate: int = 16000) -> bytes:
        """创建标准的WAV文件头"""
        num_channels = 1
        bits_per_sample = 16
        byte_rate = sample_rate * num_channels * bits_per_sample // 8
        block_align = num_channels * bits_per_sample // 8
        
        header = bytearray()
        header += b'RIFF'
        header += struct.pack('<I', 36 + data_size)  # 文件大小 - 8
        header += b'WAVE'
        header += b'fmt '
        header += struct.pack('<I', 16)  # fmt chunk大小
        header += struct.pack('<H', 1)   # 音频格式 (PCM)
        header += struct.pack('<H', num_channels)
        header += struct.pack('<I', sample_rate)
        header += struct.pack('<I', byte_rate)
        header += struct.pack('<H', block_align)
        header += struct.pack('<H', bits_per_sample)
        header += b'data'
        header += struct.pack('<I', data_size)
        
        return bytes(header)
    
    def _pcm_to_wav(self, pcm_data: bytes, sample_rate: int = 16000) -> bytes:
        """将PCM数据转换为完整的WAV文件"""
        header = self._create_wav_header(len(pcm_data), sample_rate)
        return header + pcm_data
    
    async def synthesize_stream(self, text: str, speaker_id: Optional[int] = 0, rate: float = 1.0) -> AsyncGenerator[bytes, None]:
        """流式合成语音（返回带WAV头的完整数据）"""
        if not await self.initialize():
            raise RuntimeError("TTS服务未初始化")
        
        print(f"开始流式合成语音: {text}", flush=True)
        
        # 先发送WAV头
        yield self._create_wav_header(0)  # 流式模式下头部长度为0
        
        generator = self._voxcpm.generate_streaming(
            text=text,
            reference_wav_path=self._reference_voice_path,
            cfg_value=2.0,
            inference_timesteps=10,
            normalize=True,
            denoise=False
        )
        
        for wav_chunk in generator:
            wav_int16 = (wav_chunk * 32767).astype(np.int16)
            yield wav_int16.tobytes()
    
    async def synthesize(self, text: str, speaker_id: Optional[int] = 0, rate: float = 1.0, use_clone: bool = True) -> bytes:
        """合成语音（返回完整WAV文件）
        
        参数：
            text: 要合成的文本
            speaker_id: 说话者ID
            rate: 语速
            use_clone: 是否使用语音克隆
        """
        if not await self.initialize():
            raise RuntimeError("TTS服务未初始化")
        
        print(f"合成语音: {text} (语音克隆: {use_clone})", flush=True)
        
        reference_path = self._reference_voice_path if use_clone else None
        
        # 使用VoxCPM2支持的参数
        wav = self._voxcpm.generate(
            text=text,
            reference_wav_path=reference_path,
            cfg_value=3.0,
            inference_timesteps=20,
            min_len=2,
            max_len=4096,
            normalize=True,
            denoise=True
        )
        
        wav_int16 = (wav * 32767).astype(np.int16)
        pcm_data = wav_int16.tobytes()
        
        return self._pcm_to_wav(pcm_data, self._sample_rate)
    
    async def synthesize_with_clone(self, text: str, reference_audio_path: str = None) -> bytes:
        """使用指定音色进行语音克隆
        
        参数：
            text: 要合成的文本
            reference_audio_path: 参考音频路径，不提供则使用默认参考音色
        """
        if not await self.initialize():
            raise RuntimeError("TTS服务未初始化")
        
        ref_path = reference_audio_path or self._reference_voice_path
        print(f"使用语音克隆合成: {text}, 参考音色: {ref_path}", flush=True)
        
        wav = self._voxcpm.generate(
            text=text,
            reference_wav_path=ref_path,
            cfg_value=3.0,
            inference_timesteps=20,
            min_len=2,
            max_len=4096,
            normalize=True,
            denoise=True
        )
        
        wav_int16 = (wav * 32767).astype(np.int16)
        pcm_data = wav_int16.tobytes()
        
        return self._pcm_to_wav(pcm_data, self._sample_rate)
    
    async def synthesize_and_save(self, text: str, user_id: str, conversation_id: str, filename: str = None) -> str:
        """合成语音并保存到文件，同时记录到会话缓存"""
        if not await self.initialize():
            raise RuntimeError("TTS服务未初始化")
        
        # 生成文件名
        if filename is None:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = f"{user_id}_{conversation_id}_{timestamp}.wav"
        
        output_path = os.path.join(self._output_dir, filename)
        
        # 合成语音
        wav_data = await self.synthesize(text, use_clone=True)
        
        # 保存文件
        with open(output_path, 'wb') as f:
            f.write(wav_data)
        
        # 记录到会话缓存
        await self._add_to_session_cache(user_id, conversation_id, output_path)
        
        print(f"✅ 语音已保存: {output_path}", flush=True)
        return output_path
    
    async def _add_to_session_cache(self, user_id: str, conversation_id: str, file_path: str):
        """添加语音文件到会话缓存"""
        if user_id not in self._session_cache:
            self._session_cache[user_id] = {}
        
        if conversation_id not in self._session_cache[user_id]:
            self._session_cache[user_id][conversation_id] = {
                "files": [],
                "created_at": datetime.now()
            }
        
        self._session_cache[user_id][conversation_id]["files"].append(file_path)
    
    async def cleanup_session(self, user_id: str, conversation_id: str = None):
        """清理指定用户或会话的语音文件"""
        if user_id not in self._session_cache:
            return
        
        if conversation_id:
            # 清理特定会话
            if conversation_id in self._session_cache[user_id]:
                session_data = self._session_cache[user_id][conversation_id]
                for file_path in session_data["files"]:
                    try:
                        if os.path.exists(file_path):
                            os.remove(file_path)
                            print(f"清理语音文件: {file_path}", flush=True)
                    except Exception as e:
                        print(f"清理文件失败: {file_path}, {e}", flush=True)
                del self._session_cache[user_id][conversation_id]
        else:
            # 清理用户所有会话
            for conv_id, session_data in self._session_cache[user_id].items():
                for file_path in session_data["files"]:
                    try:
                        if os.path.exists(file_path):
                            os.remove(file_path)
                            print(f"清理语音文件: {file_path}", flush=True)
                    except Exception as e:
                        print(f"清理文件失败: {file_path}, {e}", flush=True)
            del self._session_cache[user_id]
    
    async def cleanup_expired_sessions(self):
        """清理超时的会话（超过5小时）"""
        expired_sessions = []
        current_time = datetime.now()
        
        for user_id, conversations in self._session_cache.items():
            for conv_id, session_data in conversations.items():
                created_at = session_data["created_at"]
                if current_time - created_at > timedelta(hours=SESSION_TIMEOUT_HOURS):
                    expired_sessions.append((user_id, conv_id))
        
        for user_id, conv_id in expired_sessions:
            await self.cleanup_session(user_id, conv_id)
            print(f"清理超时会话: 用户 {user_id}, 会话 {conv_id}", flush=True)
    
    def get_session_info(self, user_id: str) -> Dict:
        """获取用户会话信息"""
        return self._session_cache.get(user_id, {})

# 常见问题列表
COMMON_QUESTIONS = [
    "您好，欢迎使用智能客服系统。",
    "请问有什么可以帮助您的？",
    "订单查询请进入我的订单页面。",
    "退换货政策是7天无理由退换。",
    "感谢您的使用，祝您生活愉快！",
    "物流查询请提供您的订单号。",
    "支付方式支持微信、支付宝和银行卡。",
    "发货时间一般为下单后24小时内。",
    "客服工作时间是早上9点到晚上9点。",
    "如有问题请随时联系客服。"
]

# 预测用户可能问的问题
PREDICTED_QUESTIONS = [
    "如何修改收货地址？",
    "优惠券怎么使用？",
    "商品质量有问题怎么办？",
    "如何申请退款？",
    "配送时间可以指定吗？",
    "会员积分怎么兑换？",
    "商品尺码如何选择？",
    "发票怎么开具？",
    "可以货到付款吗？",
    "如何联系人工客服？"
]