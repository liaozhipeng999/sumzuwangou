<template>
  <div class="privacy-page">
    <el-card class="privacy-card">
      <div class="privacy-header">
        <h2>隐私政策</h2>
        <p class="privacy-date">生效日期：2024年1月1日</p>
      </div>
      
      <div class="privacy-body">
        <h3>一、信息收集范围</h3>
        <p>1. 注册信息：商家名称、联系人姓名、联系电话、电子邮箱、店铺信息、营业执照信息、法人信息等。</p>
        <p>2. 经营信息：商品信息、订单记录、交易数据、客户评价、售后记录等。</p>
        <p>3. 设备信息：IP地址、浏览器类型、操作系统、设备型号、访问时间、访问路径等。</p>
        <p>4. 其他信息：商家主动提供的反馈意见、客服沟通记录等。</p>
        
        <h3>二、信息使用方式</h3>
        <p>1. 用于提供和优化平台服务，包括订单处理、客户服务、支付结算等。</p>
        <p>2. 用于风险控制和安全防范，识别和防止欺诈行为、账户异常操作等。</p>
        <p>3. 用于数据分析和业务优化，帮助商家了解经营状况、优化商品推荐等。</p>
        <p>4. 用于发送重要通知，如订单状态变更、平台公告、活动通知等。</p>
        <p>5. 用于合规审计，确保商家经营活动符合法律法规和平台规则。</p>
        
        <h3>三、信息保护措施</h3>
        <p>1. 采用SSL/TLS加密技术保护数据传输安全，防止信息被窃取或篡改。</p>
        <p>2. 严格控制数据访问权限，仅授权人员可访问必要的信息。</p>
        <p>3. 定期进行安全审计和漏洞检测，及时修复安全隐患。</p>
        <p>4. 建立完善的数据备份机制，确保数据安全和可恢复性。</p>
        <p>5. 对敏感信息进行加密存储，防止未经授权的访问。</p>
        
        <h3>四、信息共享原则</h3>
        <p>1. 不会向第三方出售、出租、转让商家的个人信息和经营信息。</p>
        <p>2. 仅在法律法规要求、司法机关调查或商家授权的情况下向第三方披露信息。</p>
        <p>3. 可能与合作方共享匿名化的统计数据，用于数据分析和业务优化。</p>
        <p>4. 平台的服务提供商可能需要访问必要的信息以提供服务，这些提供商需遵守严格的保密协议。</p>
        
        <h3>五、商家权利</h3>
        <p>1. 有权查询、更正个人信息和店铺信息。</p>
        <p>2. 有权申请注销账号并删除相关信息（法律法规另有规定除外）。</p>
        <p>3. 有权拒绝不必要的信息收集，但可能影响部分服务的使用。</p>
        <p>4. 有权了解信息的使用方式和共享范围。</p>
        
        <h3>六、Cookie政策</h3>
        <p>1. 平台使用Cookie技术来识别用户、优化服务体验。</p>
        <p>2. Cookie存储的信息包括登录状态、偏好设置、访问记录等。</p>
        <p>3. 商家可以通过浏览器设置管理Cookie，但禁用Cookie可能影响部分功能的使用。</p>
        
        <h3>七、未成年人保护</h3>
        <p>1. 平台不向未成年人提供商家注册服务。</p>
        <p>2. 如发现未成年人注册，平台有权终止服务并删除相关信息。</p>
        
        <h3>八、政策更新</h3>
        <p>1. 平台有权根据业务发展需要更新隐私政策。</p>
        <p>2. 更新后的政策将通过平台公告通知商家。</p>
        <p>3. 商家继续使用平台服务的，视为同意更新后的政策内容。</p>
        
        <h3>九、联系我们</h3>
        <p>如对本隐私政策有任何疑问或建议，可通过以下方式联系我们：</p>
        <p>- 客服热线：400-XXX-XXXX</p>
        <p>- 电子邮箱：privacy@example.com</p>
        <p>- 在线客服：平台内联系客服</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.privacy-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 40px 20px;
}

.privacy-card {
  max-width: 900px;
  margin: 0 auto;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.privacy-header {
  text-align: center;
  padding-bottom: 30px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 30px;
}

.privacy-header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 10px 0;
}

.privacy-date {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.privacy-body {
  font-size: 14px;
  line-height: 1.8;
  color: #606266;
}

.privacy-body h3 {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin: 25px 0 15px 0;
  padding-left: 10px;
  border-left: 4px solid #409eff;
}

.privacy-body p {
  margin: 10px 0;
  text-indent: 2em;
}
</style>
