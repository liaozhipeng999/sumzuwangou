import json

# 读取工作流配置
workflow_path = r"f:\temp\tow\app\comfyui\z-image_turbo.json"

with open(workflow_path, 'r', encoding='utf-8') as f:
    workflow = json.load(f)

print("📋 当前工作流配置 (z-image_turbo.json)")
print("=" * 50)

# 检查各个关键节点
for node_id, node_data in workflow.items():
    class_type = node_data.get('class_type')
    inputs = node_data.get('inputs', {})
    
    print(f"\n节点 {node_id}: {class_type}")
    
    if class_type == 'UNETLoader':
        print(f"  UNET模型: {inputs.get('unet_name')}")
        
    elif class_type == 'CLIPLoader':
        print(f"  CLIP模型: {inputs.get('clip_name')}")
        
    elif class_type == 'VAELoader':
        print(f"  VAE模型: {inputs.get('vae_name')}")
        
    elif class_type == 'KSampler':
        print(f"  采样步数: {inputs.get('steps')}")
        print(f"  CFG值: {inputs.get('cfg')}")
        print(f"  采样器: {inputs.get('sampler_name')}")
        
    elif class_type == 'EmptySD3LatentImage':
        print(f"  宽度: {inputs.get('width')}")
        print(f"  高度: {inputs.get('height')}")

print("\n✅ 当前使用的是 z-image_turbo.json 工作流")