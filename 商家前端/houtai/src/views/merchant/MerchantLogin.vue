<template>
  <div class="merchant-login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="brand-section">
          <h1>商家入驻平台</h1>
          <p>开启您的电商之旅，轻松管理您的店铺</p>
          <div class="features">
            <div class="feature-item">
              <el-icon :size="32"><Shop /></el-icon>
              <span>店铺管理</span>
            </div>
            <div class="feature-item">
              <el-icon :size="32"><Goods /></el-icon>
              <span>商品上架</span>
            </div>
            <div class="feature-item">
              <el-icon :size="32"><Document /></el-icon>
              <span>订单处理</span>
            </div>
            <div class="feature-item">
              <el-icon :size="32"><DataBoard /></el-icon>
              <span>数据分析</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="login-right">
        <el-card class="login-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h2>商家登录</h2>
              <p>请使用您的账号登录</p>
            </div>
          </template>
          
          <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
            <el-form-item prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="请输入手机号或邮箱" 
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码" 
                size="large"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item>
              <div class="form-options">
                <el-checkbox v-model="form.remember">记住账号</el-checkbox>
                <el-link type="primary" :underline="false">忘记密码？</el-link>
              </div>
            </el-form-item>
            
            <el-form-item prop="agree">
              <el-checkbox v-model="form.agree">
                我已阅读并同意 
                <el-link type="primary" @click="showServiceAgreement = true">《商家服务协议》</el-link> 
                和 
                <el-link type="primary" @click="showPrivacyPolicy = true">《隐私政策》</el-link>
              </el-checkbox>
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                style="width: 100%;" 
                :loading="loading"
                @click="handleLogin"
              >
                登录
              </el-button>
            </el-form-item>
            
            <div class="register-link">
              <span>还没有店铺？</span>
              <el-link type="primary" @click="goToRegister">立即入驻</el-link>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>
    
    <!-- 服务协议对话框 -->
    <el-dialog title="商家服务协议与隐私政策（通用电商/服务平台版）" v-model="showServiceAgreement" width="750px" top="8%">
      <div class="agreement-content">
        <div class="article-title">第一部分：商家入驻服务协议</div>
        <div class="doc-header">
          <p>协议编号：[平台简称]-FW-2026-XXXX</p>
          <p>生效日期：2026 年 6 月 11 日</p>
        </div>
        
        <h3>第一条 定义与释义</h3>
        <p><strong>平台</strong>：指由 [平台运营方名称] 运营的，为商家和消费者提供信息发布、交易撮合、支付结算、物流配套等服务的互联网平台（包括网站、APP、小程序及相关客户端）。</p>
        <p><strong>商家</strong>：指完成平台入驻流程，通过平台向消费者销售商品或提供服务的法人、非法人组织或个体工商户。</p>
        <p><strong>消费者</strong>：指通过平台购买商家商品或接受服务的自然人。</p>
        <p><strong>平台规则</strong>：指平台在官方渠道发布的所有规则、规范、公告、指引等，包括但不限于《商家入驻规则》《商品发布规范》《售后服务规则》《违规处理规则》，本协议附件及平台规则均为本协议不可分割的组成部分。</p>
        
        <h3>第二条 入驻条件与流程</h3>
        <p><strong>入驻资格</strong></p>
        <p>商家必须是依法成立并有效存续的法人、非法人组织或个体工商户，具备相应的经营资质和许可（如食品经营许可证、医疗器械经营许可证等）。</p>
        <p>商家提交的所有入驻资料必须真实、准确、完整、合法有效，不得伪造、变造或隐瞒重要信息。</p>
        <p>商家同意遵守本协议及所有平台规则，接受平台的统一管理和监督。</p>
        <p><strong>入驻流程</strong></p>
        <p>商家在线提交入驻申请及相关证明材料。</p>
        <p>平台对商家资质进行审核，审核周期一般为 3-7 个工作日。</p>
        <p>审核通过后，商家签署本协议并缴纳相应的保证金（如有）。</p>
        <p>平台为商家开通店铺后台权限，商家完成店铺装修和商品发布后即可正式营业。</p>
        <p><strong>资质更新</strong>：商家经营资质发生变更的，应在变更后 10 个工作日内通过平台后台更新相关信息，否则平台有权暂停或终止商家的店铺服务。</p>
        
        <h3>第三条 平台服务内容</h3>
        <p>为商家提供店铺开设、商品发布、订单管理、交易结算等基础服务。</p>
        <p>为商家提供营销推广工具、数据分析服务、客户管理系统等增值服务（部分服务需另行付费）。</p>
        <p>为交易双方提供支付结算服务，保障交易资金安全。</p>
        <p>协助处理商家与消费者之间的交易纠纷，维护平台正常交易秩序。</p>
        <p>平台有权根据业务发展需要调整服务内容，调整前将通过官方渠道提前 7 日通知商家。</p>
        
        <h3>第四条 商家权利与义务</h3>
        <p><strong>商家权利</strong></p>
        <p>有权在平台规则范围内自主经营店铺，发布商品信息，开展营销活动。</p>
        <p>有权获得平台提供的各项服务和技术支持。</p>
        <p>有权对平台的服务提出意见和建议。</p>
        <p>有权对平台的违规处理决定提出申诉。</p>
        <p><strong>商家义务</strong></p>
        <p>严格遵守国家法律法规及平台规则，诚信经营，不得从事任何违法违规行为。</p>
        <p>保证所销售的商品或提供的服务符合国家质量标准和安全要求，不得销售假冒伪劣商品、过期变质商品或提供虚假服务。</p>
        <p>如实描述商品信息，不得进行虚假宣传、误导性陈述或价格欺诈。</p>
        <p>按照订单约定及时发货，提供符合约定的商品和服务，履行售后服务义务。</p>
        <p>妥善处理消费者的咨询、投诉和退换货申请，不得拖延或拒绝。</p>
        <p>保护消费者个人信息安全，不得泄露、出售或非法使用消费者个人信息。</p>
        <p>按时缴纳平台服务费、技术服务费及其他约定费用。</p>
        
        <h3>第五条 费用与结算</h3>
        <p><strong>费用类型</strong></p>
        <p><strong>保证金</strong>：商家入驻时需缴纳的履约保证金，用于保障消费者权益和平台规则执行，保证金金额根据商家经营类目和店铺等级确定。</p>
        <p><strong>技术服务费</strong>：平台按照商家实际交易额的一定比例收取的服务费用，具体费率以平台公示为准。</p>
        <p><strong>增值服务费</strong>：商家使用平台提供的营销推广、数据分析等增值服务产生的费用。</p>
        <p><strong>结算方式</strong></p>
        <p>平台在消费者确认收货后 [7-15] 个工作日内，将扣除相关费用后的交易款项结算至商家绑定的银行账户。</p>
        <p>商家应自行承担因交易产生的所有税费，平台有权根据法律法规要求代扣代缴相关税费。</p>
        <p><strong>保证金管理</strong></p>
        <p>保证金在商家正常经营期间不予退还，商家申请退出平台且无未完成交易、未处理纠纷及违规记录的，平台将在退出申请审核通过后 [30-90] 个工作日内无息退还保证金。</p>
        <p>商家违反本协议或平台规则给平台或消费者造成损失的，平台有权直接从保证金中扣除相应金额用于赔偿。</p>
        
        <h3>第六条 商品与交易管理</h3>
        <p><strong>商品发布</strong>：商家发布的商品信息必须真实、准确、完整，包含商品名称、规格、价格、产地、生产日期、保质期、售后服务等必要信息。</p>
        <p><strong>价格管理</strong>：商家应明码标价，不得虚构原价、虚假打折或进行价格欺诈。商品价格变动应及时更新，避免因价格错误产生纠纷。</p>
        <p><strong>订单管理</strong>：商家收到消费者订单后，应及时确认并按照订单约定发货。不得无故取消订单或拒绝发货，确因特殊原因无法履行订单的，应及时与消费者沟通并承担相应责任。</p>
        <p><strong>物流管理</strong>：商家应选择合法合规的物流企业，确保商品安全、及时送达。发货后应及时上传物流单号，跟踪物流状态。</p>
        
        <h3>第七条 售后服务与纠纷处理</h3>
        <p><strong>售后服务</strong>：商家应按照国家 "三包" 规定及平台售后服务规则，为消费者提供退换货、维修等售后服务。</p>
        <p><strong>纠纷处理</strong></p>
        <p>商家与消费者发生交易纠纷时，应首先自行协商解决。</p>
        <p>协商不成的，任何一方均可申请平台介入调解。平台将根据双方提交的证据和平台规则作出调解决定。</p>
        <p>对平台调解决定不服的，可向消费者协会投诉或通过法律途径解决。</p>
        <p><strong>先行赔付</strong>：商家违反本协议或平台规则给消费者造成损失，且商家拒绝赔偿或无力赔偿的，平台有权先行向消费者赔付，再向商家追偿。</p>
        
        <h3>第八条 违规处理</h3>
        <p>商家违反本协议或平台规则的，平台有权根据违规情节轻重采取以下一项或多项措施：</p>
        <p>警告、责令整改；下架违规商品；限制店铺权限（如限制发布商品、限制营销活动）；扣除保证金；暂停店铺服务；永久封禁店铺。</p>
        <p>商家因违规行为给平台造成损失的，应承担全部赔偿责任，包括但不限于直接损失、间接损失、律师费、诉讼费等。</p>
        
        <h3>第九条 协议的变更、解除与终止</h3>
        <p><strong>协议变更</strong>：平台有权根据法律法规变化和业务发展需要修改本协议，修改后的协议将通过官方渠道提前 7 日通知商家。商家继续使用平台服务的，视为同意修改后的协议。</p>
        <p><strong>协议解除</strong></p>
        <p>商家提前 30 日向平台提交书面申请，经平台审核同意后可解除本协议。</p>
        <p>商家有下列情形之一的，平台有权单方解除本协议：提供虚假入驻资料；销售假冒伪劣商品或从事其他严重违法违规行为；拖欠平台费用超过 30 日；店铺连续 90 日无交易且未登录后台；其他严重违反本协议或平台规则的行为。</p>
        <p><strong>协议终止</strong>：本协议终止后，商家应立即停止使用平台服务，处理完所有未完成的交易和售后服务。平台将按照本协议约定结算剩余款项并退还保证金（如有）。</p>
        
        <h3>第十条 知识产权</h3>
        <p>平台上所有内容（包括但不限于文字、图片、视频、软件、商标、标识等）的知识产权均归平台或相关权利人所有。</p>
        <p>商家保证其发布的商品信息和使用的素材不侵犯任何第三方的知识产权。如因商家侵权导致平台遭受损失的，商家应承担全部赔偿责任。</p>
        
        <h3>第十一条 保密条款</h3>
        <p>双方应对在合作过程中获悉的对方商业秘密和保密信息承担保密义务，未经对方书面同意，不得向任何第三方泄露。</p>
        <p>本保密义务在本协议终止后仍然有效。</p>
        
        <h3>第十二条 免责条款</h3>
        <p>因不可抗力（包括但不限于自然灾害、战争、政府行为、黑客攻击、网络故障等）导致平台无法正常提供服务的，平台不承担任何责任。</p>
        <p>平台仅提供交易撮合服务，不对商家商品或服务的质量、安全承担保证责任。</p>
        <p>平台对因商家原因导致的任何损失不承担责任。</p>
        
        <h3>第十三条 争议解决</h3>
        <p>本协议的签订、履行、解释及争议解决均适用中华人民共和国法律。</p>
        <p>因本协议产生的任何争议，双方应首先友好协商解决；协商不成的，任何一方均可向平台运营方所在地人民法院提起诉讼。</p>
        
        <h3>第十四条 其他</h3>
        <p>本协议自商家点击 "同意" 按钮并完成入驻流程之日起生效。</p>
        <p>本协议未尽事宜，由双方另行协商解决，或按照平台规则执行。</p>
        <p>本协议一式两份，双方各执一份，具有同等法律效力。</p>
      </div>
    </el-dialog>
    
    <!-- 隐私政策对话框 -->
    <el-dialog title="平台隐私政策" v-model="showPrivacyPolicy" width="750px" top="8%">
      <div class="agreement-content">
        <div class="article-title">平台隐私政策</div>
        <div class="doc-header">
          <p>更新日期：2026 年 6 月 11 日</p>
          <p>生效日期：2026 年 6 月 11 日</p>
        </div>
        <p>[平台运营方名称]（以下简称 "我们"）非常重视用户的个人信息保护，将按照法律法规要求，采取相应的安全保护措施，保护您的个人信息安全可控。本隐私政策旨在向您说明我们如何收集、使用、存储、共享和保护您的个人信息，以及您享有的相关权利。</p>
        
        <h3>一、我们收集的个人信息</h3>
        <p>我们会遵循 "合法、正当、必要" 的原则，仅收集为实现服务功能所必需的个人信息。</p>
        
        <p><strong>1. 您主动提供的信息</strong></p>
        <p><strong>注册登录信息</strong>：当您注册平台账号时，我们会收集您的手机号码、电子邮箱、用户名、密码等信息。</p>
        <p><strong>实名认证信息</strong>：当您进行实名认证时，我们会收集您的真实姓名、身份证号码、面部识别信息等。</p>
        <p><strong>收货地址信息</strong>：当您添加收货地址时，我们会收集您的收货人姓名、手机号码、详细地址等信息。</p>
        <p><strong>支付信息</strong>：当您进行支付时，我们会收集您的支付账号、支付金额、交易时间等信息（具体支付信息由第三方支付机构收集和处理）。</p>
        <p><strong>订单信息</strong>：当您购买商品或服务时，我们会收集您的订单编号、商品信息、交易金额、下单时间、收货地址等信息。</p>
        <p><strong>沟通信息</strong>：当您与商家或平台客服沟通时，我们会收集您的聊天记录、通话录音等信息。</p>
        <p><strong>其他信息</strong>：您在使用平台服务过程中主动提交的其他信息，如评价、晒单、投诉内容等。</p>
        
        <p><strong>2. 我们自动收集的信息</strong></p>
        <p><strong>设备信息</strong>：我们会收集您使用的设备型号、操作系统版本、设备标识符（IMEI、Android ID、IDFA 等）、网络状态、IP 地址等信息。</p>
        <p><strong>使用信息</strong>：我们会收集您的访问时间、页面浏览记录、点击记录、搜索记录、商品收藏记录等信息。</p>
        <p><strong>位置信息</strong>：在您授权的情况下，我们会收集您的地理位置信息，用于提供基于位置的服务（如附近商家推荐、物流跟踪）。您可以随时关闭位置权限，停止我们收集您的地理位置信息。</p>
        
        <p><strong>3. 第三方提供的信息</strong></p>
        <p>在您授权的情况下，我们可能会从第三方合作伙伴处获取您的相关信息，例如：</p>
        <p>当您使用第三方账号登录平台时，我们会获取您在第三方平台的公开信息（如昵称、头像）。</p>
        <p>当您使用第三方支付服务时，我们会获取您的支付结果信息。</p>
        
        <h3>二、我们如何使用您的个人信息</h3>
        <p>我们会将收集到的个人信息用于以下目的：</p>
        <p>提供基础服务：用于创建和管理您的账号、处理订单、完成支付、安排物流、提供售后服务等。</p>
        <p>改善服务体验：用于分析您的使用习惯，优化平台界面和功能，为您提供个性化的推荐和服务。</p>
        <p>保障交易安全：用于身份验证、安全防范、反欺诈、风险控制等，保护您和平台的合法权益。</p>
        <p>发送通知信息：用于向您发送订单状态通知、物流信息、活动推广信息等。您可以随时取消订阅推广信息。</p>
        <p>合规要求：用于遵守法律法规要求，配合监管部门调查等。</p>
        
        <h3>三、我们如何共享、转让和公开披露您的个人信息</h3>
        
        <p><strong>1. 信息共享</strong></p>
        <p>我们不会向任何无关第三方共享您的个人信息，除非：</p>
        <p>事先获得您的明确同意。</p>
        <p>与我们的关联公司共享：我们可能会与我们的关联公司共享您的个人信息，但仅用于本隐私政策声明的目的。</p>
        <p>与授权合作伙伴共享：我们可能会委托授权合作伙伴为您提供某些服务，例如支付服务、物流服务、营销服务等。我们仅会共享提供服务所必需的个人信息，并要求合作伙伴严格按照本隐私政策和相关法律法规处理您的个人信息。</p>
        <p>法律法规要求：根据法律法规规定、司法机关或行政机关的强制性要求，我们可能会共享您的个人信息。</p>
        
        <p><strong>2. 信息转让</strong></p>
        <p>我们不会转让您的个人信息，除非：</p>
        <p>事先获得您的明确同意。</p>
        <p>因公司合并、收购、破产清算等原因需要转让个人信息的，我们会提前通知您，并要求受让方继续受本隐私政策的约束。</p>
        
        <p><strong>3. 信息公开披露</strong></p>
        <p>我们不会公开披露您的个人信息，除非：</p>
        <p>事先获得您的明确同意。</p>
        <p>为保护您、平台或社会公众的合法权益，在法律允许的范围内披露。</p>
        
        <h3>四、我们如何存储和保护您的个人信息</h3>
        
        <p><strong>1. 信息存储</strong></p>
        <p>我们会将您的个人信息存储在中华人民共和国境内，不会向境外传输您的个人信息，除非事先获得您的明确同意并符合法律法规要求。</p>
        <p>我们只会在实现服务目的所必需的最短期限内存储您的个人信息。超出存储期限后，我们会对您的个人信息进行删除或匿名化处理。</p>
        
        <p><strong>2. 信息保护</strong></p>
        <p>我们会采用符合行业标准的安全技术措施（如加密技术、访问控制、防火墙等）保护您的个人信息，防止信息泄露、篡改或丢失。</p>
        <p>我们会建立完善的信息安全管理制度，对接触您个人信息的工作人员进行严格的权限管理和安全培训。</p>
        <p>尽管我们会采取上述安全措施，但请您理解，互联网环境并非绝对安全，我们无法保证您的个人信息 100% 安全。</p>
        
        <h3>五、您的个人信息权利</h3>
        <p>根据法律法规要求，您享有以下个人信息权利：</p>
        <p><strong>知情权</strong>：您有权了解我们收集、使用、存储、共享您个人信息的情况。</p>
        <p><strong>访问权</strong>：您有权访问您的个人信息，包括账号信息、订单信息、收货地址信息等。</p>
        <p><strong>更正权</strong>：当您发现我们收集的您的个人信息有误时，您有权要求我们进行更正。</p>
        <p><strong>删除权</strong>：在符合法律法规规定的情形下，您有权要求我们删除您的个人信息。</p>
        <p><strong>撤回同意权</strong>：您有权随时撤回您之前给予的个人信息收集和使用的同意。撤回同意后，我们将不再处理相应的个人信息，但不会影响此前基于您的同意已进行的信息处理。</p>
        <p><strong>注销权</strong>：您有权注销您的平台账号。账号注销后，我们将停止为您提供服务，并按照法律法规要求删除您的个人信息。</p>
        <p><strong>投诉举报权</strong>：如果您认为我们的个人信息处理行为违反了法律法规或本隐私政策，您有权向相关监管部门投诉举报。</p>
        
        <h3>六、未成年人个人信息保护</h3>
        <p>我们非常重视未成年人的个人信息保护。如果您是未满 18 周岁的未成年人，请在监护人的陪同下阅读本隐私政策，并在监护人同意的情况下使用我们的服务。</p>
        <p>对于经监护人同意收集的未成年人个人信息，我们只会在法律法规允许、监护人明确同意或保护未成年人所必需的范围内使用和披露。</p>
        <p>如果我们发现未经监护人同意收集了未成年人的个人信息，我们会立即删除相关信息。</p>
        
        <h3>七、本隐私政策的更新</h3>
        <p>我们可能会根据法律法规变化和业务发展需要，不定期更新本隐私政策。更新后的隐私政策将通过平台官方渠道发布，并会注明更新日期和生效日期。如果您在本隐私政策更新后继续使用我们的服务，即表示您同意更新后的隐私政策。</p>
        
        <h3>八、联系我们</h3>
        <p>如果您对本隐私政策有任何疑问、意见或建议，或者需要行使您的个人信息权利，请通过以下方式联系我们：</p>
        <p>客服电话：[平台客服电话]</p>
        <p>电子邮箱：[平台客服邮箱]</p>
        <p>联系地址：[平台运营方地址]</p>
        <p>我们会在收到您的请求后 15 个工作日内予以回复。</p>
        
        <h3>毕业设计使用说明</h3>
        <p><strong>替换占位符</strong>：将文档中所有[ ]内的内容替换为您毕业设计中的平台名称、运营方信息、具体参数等。</p>
        <p><strong>个性化调整</strong>：</p>
        <p>如果是本地生活平台，增加 "到店服务"" 团购核销 " 等相关条款</p>
        <p>如果是知识付费平台，增加 "内容版权"" 虚拟商品不支持退换 " 等条款</p>
        <p>如果是跨境电商平台，增加 "海关申报"" 跨境物流 ""关税说明" 等条款</p>
        <p><strong>合规性提示</strong>：正式上线使用时，建议咨询专业律师，根据最新法律法规和具体业务场景进行调整。</p>
        <p><strong>格式建议</strong>：提交毕业设计时，可以将两份文档分别保存为独立文件，并在封面注明 "商家服务协议" 和 "平台隐私政策"。</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Shop, Goods, Document, DataBoard, User, Lock } from '@element-plus/icons-vue'
import { useMerchantStore } from '@/stores/merchant'

const router = useRouter()
const merchantStore = useMerchantStore()
const formRef = ref()
const loading = ref(false)

// 协议对话框状态
const showServiceAgreement = ref(false)
const showPrivacyPolicy = ref(false)

const form = reactive({
  username: '',
  password: '',
  remember: false,
  agree: false
})

// 判断是手机号还是邮箱
function isPhone(value: string): boolean {
  return /^1[3-9]\d{9}$/.test(value)
}

function isEmail(value: string): boolean {
  // 宽松的邮箱验证，允许仿造邮箱
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)
}

const rules = {
  username: [
    { required: true, message: '请输入手机号或邮箱', trigger: 'blur' },
    { validator: (_rule: any, value: string, callback: any) => {
      if (!isPhone(value) && !isEmail(value)) {
        callback(new Error('请输入正确的手机号或邮箱'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  agree: [
    { validator: (_rule: any, value: boolean, callback: any) => {
        if (!value) {
          callback(new Error('请先同意服务协议和隐私政策'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 页面加载时检查是否有记住的账号
onMounted(() => {
  const savedUsername = localStorage.getItem('merchant_username')
  if (savedUsername) {
    form.username = savedUsername
    form.remember = true
  }
})

const handleLogin = async () => {
  if (!formRef.value) {
    ElMessage.error('❌ 表单初始化失败，请刷新页面重试')
    return
  }
  
  // 验证表单
  const validateResult = await formRef.value.validate()
  if (!validateResult) {
    return
  }
  
  // 输出验证结果到控制台
  if (isPhone(form.username)) {
    console.log('📱 检测到手机号登录:', form.username)
  } else if (isEmail(form.username)) {
    console.log('📧 检测到邮箱登录:', form.username)
  }
  
  loading.value = true
  
  try {
    console.log('📤 发送登录请求:', { loginId: form.username })
    
    const result = await merchantStore.login(form.username, form.password)
    
    if (result.success) {
      // 记住账号
      if (form.remember) {
        localStorage.setItem('merchant_username', form.username)
      } else {
        localStorage.removeItem('merchant_username')
      }
      
      ElMessage.success('🎉 登录成功！')
      router.push('/merchant/dashboard')
    } else {
      console.error('❌ 登录失败:', result.message)
      ElMessage.error(`❌ ${result.message}`)
    }
  } catch (error: any) {
    console.error('❌ 登录异常:', error)
    ElMessage.error(`❌ 登录异常：${error.message || '网络错误，请检查后端服务是否运行'}`)
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/merchant/register')
}
</script>

<style scoped>
.merchant-login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  display: flex;
  width: 1000px;
  max-width: 100%;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.login-left {
  width: 450px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.brand-section {
  text-align: center;
}

.brand-section h1 {
  font-size: 32px;
  margin-bottom: 20px;
  font-weight: bold;
}

.brand-section p {
  font-size: 16px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.features {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.feature-item span {
  font-size: 14px;
}

.login-right {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-card {
  border: none;
  box-shadow: none;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.card-header p {
  font-size: 14px;
  color: #666;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.register-link .el-link {
  margin-left: 5px;
}

.login-footer {
  margin-top: 30px;
  text-align: center;
  font-size: 12px;
  color: #999;
}

.login-footer .el-link {
  font-size: 12px;
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }
  
  .login-left {
    width: 100%;
    padding: 40px 20px;
  }
  
  .login-right {
    padding: 30px 20px;
  }
  
  .features {
    gap: 20px;
  }
}

.agreement-content {
  max-height: 500px;
  overflow-y: auto;
  padding: 20px 25px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.agreement-content::-webkit-scrollbar {
  width: 6px;
}

.agreement-content::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.agreement-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.agreement-content::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.agreement-content .doc-header {
  text-align: center;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 2px solid #e2e8f0;
}

.agreement-content .doc-header p {
  font-size: 13px;
  color: #94a3b8;
  margin: 5px 0;
  text-indent: 0;
}

.agreement-content h3 {
  font-size: 16px;
  color: #1e293b;
  margin: 24px 0 14px 0;
  font-weight: 600;
  padding-left: 12px;
  border-left: 4px solid #6366f1;
  background: #ffffff;
  padding: 10px 15px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.agreement-content h3:first-child {
  margin-top: 0;
}

.agreement-content p {
  font-size: 14px;
  color: #475569;
  line-height: 2.2;
  margin: 16px 0;
  text-indent: 2em;
  letter-spacing: 0.3px;
  word-break: break-word;
  overflow-wrap: break-word;
  white-space: pre-wrap;
  display: block;
}

.agreement-content p strong {
  color: #1e293b;
  font-weight: 600;
}

.agreement-content .article-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  text-align: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #cbd5e1;
}

.agreement-content .highlight-box {
  background: #fffbeb;
  border-left: 4px solid #f59e0b;
  padding: 12px 16px;
  margin: 16px 0;
  border-radius: 0 8px 8px 0;
}

.agreement-content .highlight-box p {
  margin: 0;
  text-indent: 0;
  color: #92400e;
  font-size: 13px;
}

.agreement-content .section-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, #cbd5e1, transparent);
  margin: 24px 0;
}
</style>