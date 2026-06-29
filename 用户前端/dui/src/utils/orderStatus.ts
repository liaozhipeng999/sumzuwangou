// 订单状态兼容层
// 后端曾经用过两套 status 枚举：
//   旧：0待付款 1已付款 2已发货 3已完成 4已取消
//   新：1待付款 2待发货 3待收货 4已完成 5已取消
// 新后端返回的 status 已经是新枚举。兼容函数：检测到旧枚举 (0/1/2/3/4) 自动映射到新枚举 (1/2/3/4/5)。

const LEGACY_TO_NEW: Record<number, number> = {
  0: 1, // 旧"待付款" → 新"待付款"
  1: 2, // 旧"已付款" → 新"待发货"
  2: 3, // 旧"已发货" → 新"待收货"
  3: 4, // 旧"已完成" → 新"已完成"
  4: 5  // 旧"已取消" → 新"已取消"
}

const ORDER_STATUS_LABEL: Record<number, string> = {
  1: '待付款',
  2: '待发货',
  3: '待收货',
  4: '已完成',
  5: '已取消'
}

export const ORDER_STATUS = {
  UNPAID: 1,
  SHIPPING: 2,
  RECEIVING: 3,
  COMPLETED: 4,
  CANCELLED: 5
} as const

export function normalizeStatus(status: number | undefined | null): number {
  if (status == null) return 1
  if (LEGACY_TO_NEW[status] != null) return LEGACY_TO_NEW[status]
  return status
}

export function getStatusLabel(status: number | undefined | null): string {
  const s = normalizeStatus(status)
  return ORDER_STATUS_LABEL[s] || '未知状态'
}

export { ORDER_STATUS_LABEL }
