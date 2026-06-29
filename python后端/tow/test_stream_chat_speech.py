"""测试流式聊天接口（支持语音外放）"""
import requests
import json
import time

def test_stream_chat_with_speech():
    url = "http://localhost:8000/api/chat/stream"
    
    payload = {
        "user_id": "test_user_001",
        "message": "你好，请介绍一下你们的售后服务政策",
        "conversation_id": None,
        "speech": True,  # 启用语音输出
        "speaker_id": 0,
        "rate": 1.0
    }
    
    print("=" * 60)
    print("测试流式聊天接口（支持语音外放）")
    print("=" * 60)
    print(f"用户消息: {payload['message']}")
    print(f"启用语音: {payload['speech']}")
    print("-" * 60)
    
    try:
        response = requests.post(url, json=payload, stream=True, timeout=120)
        
        if response.status_code != 200:
            print(f"❌ 请求失败，状态码: {response.status_code}")
            print(response.text)
            return
        
        print(f"✅ 请求成功，状态码: {response.status_code}")
        print("-" * 60)
        
        full_response = ""
        token_count = 0
        speech_data = None
        speech_size = 0
        conversation_id = None
        sources = []
        speech_errors = []
        
        start_time = time.time()
        
        for line in response.iter_lines():
            if line:
                line = line.decode('utf-8')
                if line.startswith('data: '):
                    data_str = line[6:]
                    try:
                        data = json.loads(data_str)
                        event_type = data.get('type')
                        
                        if event_type == 'sources':
                            sources = data.get('data', [])
                            print(f"📚 知识库来源: {len(sources)} 个文档")
                        
                        elif event_type == 'token':
                            token = data.get('data', '')
                            full_response += token
                            token_count += 1
                            # 实时显示（每10个字换行）
                            if token_count % 10 == 0:
                                print(f"\n  已接收 {token_count} 个字符...")
                        
                        elif event_type == 'speech':
                            speech_data = data.get('data')
                            speech_size = data.get('size', 0)
                            sample_rate = data.get('sample_rate', 0)
                            print(f"\n🎤 收到语音数据:")
                            print(f"   - 语音大小: {speech_size} 字节 ({speech_size/1024:.2f} KB)")
                            print(f"   - 采样率: {sample_rate} Hz")
                            print(f"   - Hex编码长度: {len(speech_data)} 字符")
                        
                        elif event_type == 'speech_error':
                            error_msg = data.get('data')
                            speech_errors.append(error_msg)
                            print(f"⚠️ 语音生成错误: {error_msg}")
                        
                        elif event_type == 'done':
                            conversation_id = data.get('data', {}).get('conversation_id')
                            elapsed = time.time() - start_time
                            print("-" * 60)
                            print("✅ 流式输出完成!")
                            print(f"   - 总字数: {len(full_response)} 字符")
                            print(f"   - Token数量: {token_count}")
                            print(f"   - 会话ID: {conversation_id}")
                            print(f"   - 耗时: {elapsed:.2f} 秒")
                            if sources:
                                print(f"   - 知识库来源: {len(sources)} 个文档")
                            if speech_size > 0:
                                print(f"   - 语音大小: {speech_size/1024:.2f} KB")
                            if speech_errors:
                                print(f"   - 语音错误: {len(speech_errors)} 个")
                        
                        elif event_type == 'error':
                            print(f"❌ 错误: {data.get('data')}")
                    
                    except json.JSONDecodeError as e:
                        print(f"解析错误: {e}, 原始数据: {data_str[:100]}")
        
        print("=" * 60)
        print("\n📝 AI 回复内容:")
        print("-" * 60)
        print(full_response[:500] + "..." if len(full_response) > 500 else full_response)
        print("-" * 60)
        
        if speech_data and not speech_errors:
            # 保存语音文件
            try:
                audio_bytes = bytes.fromhex(speech_data)
                audio_file = "test_output.wav"
                with open(audio_file, 'wb') as f:
                    f.write(audio_bytes)
                print(f"✅ 语音已保存到: {audio_file}")
                print(f"   文件大小: {len(audio_bytes)/1024:.2f} KB")
            except Exception as e:
                print(f"❌ 保存语音文件失败: {e}")
        
        return {
            'success': True,
            'response': full_response,
            'conversation_id': conversation_id,
            'token_count': token_count,
            'speech_size': speech_size,
            'sources': sources
        }
    
    except requests.exceptions.Timeout:
        print("❌ 请求超时")
        return {'success': False, 'error': '请求超时'}
    except Exception as e:
        print(f"❌ 测试失败: {e}")
        return {'success': False, 'error': str(e)}

if __name__ == "__main__":
    result = test_stream_chat_with_speech()
    print("\n" + "=" * 60)
    if result and result.get('success'):
        print("🎉 测试通过!")
    else:
        print("❌ 测试失败")
