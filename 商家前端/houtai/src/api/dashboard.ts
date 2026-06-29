import api from './merchant'

export interface DashboardStatCards {
  todaySales: number
  todayOrders: number
  pendingShip: number
  totalOrders: number
  totalSales: number
  ordersGrowth: number
  salesGrowth: number
}

export interface TrendPoint {
  labels: string[]
  sales: number[]
  orders: number[]
}

export interface TopProduct {
  name: string
  sales: number
  stock: number
}

export interface CategoryStat {
  name: string
  value: number
}

export interface RecentOrder {
  orderNo: string
  totalAmount: number
  status: string
  statusText: string
  createTime: string
  customerName: string
}

export interface DashboardData {
  statCards: DashboardStatCards
  trend: TrendPoint[]
  topProducts: TopProduct[]
  categories: CategoryStat[]
  recentOrders: RecentOrder[]
}

export function getDashboardStats(shopId: number, range: 'week' | 'month' | 'year' = 'week') {
  return api.get<any, any>('/merchant/dashboard/stats', { params: { shopId, range } })
    .then((r: any) => {
      if (r?.code === 200) return r.data as DashboardData
      throw new Error(r?.message || '获取看板数据失败')
    })
}
