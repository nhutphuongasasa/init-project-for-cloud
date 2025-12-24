"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { ChevronLeft, ChevronRight, MoreHorizontal, Search } from "lucide-react"
import { Header } from "@/components/custom/header"
import { InboundOrder, InboundOrderHeaders, InboundStatusConfig, Order, OrderHeaders, OrderStatusConfig, OrderType, SimpleStatusMap } from "@/interface/order"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuSub, DropdownMenuSubContent, DropdownMenuSubTrigger, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import api from "@/lib/axios"
import { useToast } from "@/hooks/use-toast"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"

export default function AdminOrdersPage() {
  const router = useRouter()
  const [searchTerm, setSearchTerm] = useState("")
  const [activeTab, setActiveTab] = useState<OrderType>("outbound")

  // Outbound states
  const [outboundOrders, setOutboundOrders] = useState<Order[]>([])
  const [outboundPage, setOutboundPage] = useState(0)
  const [outboundTotalPages, setOutboundTotalPages] = useState(1)

  // Inbound states
  const [inboundOrders, setInboundOrders] = useState<InboundOrder[]>([])
  const [inboundPage, setInboundPage] = useState(0)
  const [inboundTotalPages, setInboundTotalPages] = useState(1)

  const { toast } = useToast()

  // Stats (chỉ tính trên trang hiện tại – nếu cần total thật thì phải có API riêng)
  const outboundStats = {
    total: outboundOrders.length,
    pending: outboundOrders.filter((o) => o.status === "CREATED").length,
    inTransit: outboundOrders.filter((o) =>
      ["PICKING", "PICKED", "PACKING", "PACKED"].includes(o.status)
    ).length,
    shipped: outboundOrders.filter((o) => o.status === "SHIPPED").length,
  }

  const inboundStats = {
    total: inboundOrders.length,
    draft: inboundOrders.filter((o) => o.status === "DRAFT").length,
    receiving: inboundOrders.filter((o) => o.status === "RECEIVING").length,
    received: inboundOrders.filter((o) => o.status === "RECEIVED").length,
  }

  // ================== ACTIONS (giữ nguyên như cũ) ==================
  const approveOrder = async (orderId: string, vendorId: string) => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/approve`,
        { orderId, vendorId },
        { withCredentials: true }
      )
      toast({ title: "Order approved successfully" })
      fetchOrders(activeTab, activeTab === "outbound" ? outboundPage : inboundPage)
    } catch (error) {
      toast({ title: "Approve failed", variant: "destructive" })
    }
  }

  const shipOrder = async (orderId: string) => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/ship/${orderId}`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Order shipped successfully" })
      fetchOrders(activeTab, activeTab === "outbound" ? outboundPage : inboundPage)
    } catch (error) {
      toast({ title: "Ship failed", variant: "destructive" })
    }
  }

  const cancelOrder = async (orderId: string) => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/cancel/${orderId}`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Order cancelled successfully" })
      fetchOrders(activeTab, activeTab === "outbound" ? outboundPage : inboundPage)
    } catch (error) {
      toast({ title: "Cancel failed", variant: "destructive" })
    }
  }

  const startPicking = async (orderId: string) => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${orderId}/start-picking`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Picking started" })
      router.push(`/admin/orders/${orderId}`)
    } catch (error) {
      toast({ title: "Start picking failed", variant: "destructive" })
    }
  }

  const completePicking = async (orderId: string) => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${orderId}/complete-picking`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Picking completed" })
      fetchOrders(activeTab, activeTab === "outbound" ? outboundPage : inboundPage)
    } catch (error) {
      toast({ title: "Complete picking failed", variant: "destructive" })
    }
  }

  const startPacking = async (orderId: string) => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${orderId}/start-packing`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Packing started" })
      fetchOrders(activeTab, activeTab === "outbound" ? outboundPage : inboundPage)
    } catch (error) {
      toast({ title: "Start packing failed", variant: "destructive" })
    }
  }

  const completePacking = async (orderId: string) => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${orderId}/complete-packing/`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Packing completed" })
      fetchOrders(activeTab, activeTab === "outbound" ? outboundPage : inboundPage)
    } catch (error) {
      toast({ title: "Complete packing failed", variant: "destructive" })
    }
  }

  const confirmInbound = async (inboundId: string) => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/inbound-command/${inboundId}/confirm`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Inbound order confirmed" })
      fetchOrders(activeTab, activeTab === "outbound" ? outboundPage : inboundPage)
    } catch (error) {
      toast({ title: "Confirm failed", variant: "destructive" })
    }
  }

  const startReceiving = async (inboundId: string) => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/inbound-command/${inboundId}/start-receiving`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Receiving started" })
      fetchOrders(activeTab, activeTab === "outbound" ? outboundPage : inboundPage)
    } catch (error) {
      toast({ title: "Start receiving failed", variant: "destructive" })
    }
  }

  const completeReceiving = async (inboundId: string) => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/inbound-command/${inboundId}/complete-receiving`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Receiving completed" })
      fetchOrders(activeTab, activeTab === "outbound" ? outboundPage : inboundPage)
    } catch (error) {
      toast({ title: "Complete receiving failed", variant: "destructive" })
    }
  }

  const cancelInbound = async (inboundId: string) => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/inbound-command/${inboundId}/cancel`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Inbound order cancelled" })
      fetchOrders(activeTab, activeTab === "outbound" ? outboundPage : inboundPage)
    } catch (error) {
      toast({ title: "Cancel failed", variant: "destructive" })
    }
  }

  // ================== FETCH ORDERS ==================
  const fetchOrders = async (type: OrderType, page: number = 0) => {
    try {
      const endpoint =
        type === "outbound"
          ? `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query/all?page=${page}&size=1`
          : `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query/inbound?page=${page}&size=1`

      const res = await api.get(endpoint, { withCredentials: true })
      const pageData = res.data?.data || {}
      const content = pageData.content || []
      const totalPages = pageData.totalPages || 1

      if (type === "outbound") {
        setOutboundOrders(content)
        setOutboundPage(page)
        setOutboundTotalPages(totalPages)
      } else {
        setInboundOrders(content)
        setInboundPage(page)
        setInboundTotalPages(totalPages)
      }
    } catch (error) {
      toast({ title: "Failed to load orders", variant: "destructive" })
    }
  }

  // Load data khi đổi tab
  useEffect(() => {
    fetchOrders(activeTab, 0)
  }, [activeTab])

  // Action maps
  const OutboundStatusActionMap: Record<string, (order: Order) => Promise<void>> = {
    APPROVED: async (order) => {
      if (!order.vendorId) {
        toast({ title: "Missing vendor ID", variant: "destructive" })
        return
      }
      await approveOrder(order.id, order.vendorId)
    },
    CANCELLED: async (order) => await cancelOrder(order.id),
    SHIPPED: async (order) => await shipOrder(order.id),
    PICKING: async (order) => await startPicking(order.id),
    PICKED: async (order) => await completePicking(order.id),
    PACKING: async (order) => await startPacking(order.id),
    PACKED: async (order) => await completePacking(order.id),
  }

  const InboundStatusActionMap: Record<string, (order: InboundOrder) => Promise<void>> = {
    CONFIRMED: async (order) => await confirmInbound(order.id),
    RECEIVING: async (order) => await startReceiving(order.id),
    RECEIVED: async (order) => await completeReceiving(order.id),
    CANCELLED: async (order) => await cancelInbound(order.id),
  }

  // ================== PAGINATION COMPONENT ==================
  const PaginationControls = ({
    currentPage,
    totalPages,
    onPageChange,
  }: {
    currentPage: number
    totalPages: number
    onPageChange: (page: number) => void
  }) => {
    if (totalPages <= 1) return null

    return (
      <div className="flex items-center justify-between mt-8 border-t pt-4">
        <div className="text-sm text-muted-foreground">
          Page {currentPage + 1} / {totalPages}
        </div>
        <div className="flex gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => onPageChange(currentPage - 1)}
            disabled={currentPage === 0}
          >
            <ChevronLeft className="w-4 h-4 mr-1" />
            Previous
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => onPageChange(currentPage + 1)}
            disabled={currentPage >= totalPages - 1}
          >
            Next
            <ChevronRight className="w-4 h-4 ml-1" />
          </Button>
        </div>
      </div>
    )
  }

  // ================== OUTBOUND TABLE ==================
  const renderOutboundTable = (orders: Order[]) => (
    <Card>
      <CardHeader>
        <CardTitle>Outbound Orders</CardTitle>
        <CardDescription>Track and manage all customer orders</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b">
                {Object.keys(OrderHeaders).map((key) => (
                  <th key={key} className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">
                    {OrderHeaders[key as keyof typeof OrderHeaders]}
                  </th>
                ))}
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Action</th>
              </tr>
            </thead>
            <tbody>
              {orders.length === 0 ? (
                <tr>
                  <td colSpan={Object.keys(OrderHeaders).length + 1} className="text-center py-12 text-muted-foreground">
                    No orders found
                  </td>
                </tr>
              ) : (
                orders.map((order) => (
                  <tr key={order.id} className="border-b hover:bg-muted/50">
                    {Object.keys(OrderHeaders).map((key) => {
                      if (key === "status") {
                        const config = OrderStatusConfig[order.status]
                        return (
                          <td key={key} className="py-4 px-4">
                            <div className="flex justify-center">
                              {config ? (
                                <span className={`px-3 py-1 rounded-full text-xs font-semibold ${config.bg} ${config.text}`}>
                                  {config.label}
                                </span>
                              ) : (
                                <span className="px-3 py-1 rounded-full bg-gray-200 text-gray-600 text-xs">
                                  {order.status}
                                </span>
                              )}
                            </div>
                          </td>
                        )
                      }
                      return (
                        <td key={key} className="py-4 px-4 text-sm text-muted-foreground truncate max-w-[180px]">
                          {String(order[key as keyof Order])}
                        </td>
                      )
                    })}
                    <td className="py-4 px-4">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon">
                            <MoreHorizontal className="w-5 h-5" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuLabel>Actions</DropdownMenuLabel>
                          <DropdownMenuSeparator />
                          <DropdownMenuItem onClick={() => router.push(`/admin/orders/${order.id}`)}>
                            View Details
                          </DropdownMenuItem>
                          <DropdownMenuSub>
                            <DropdownMenuSubTrigger>Change Status</DropdownMenuSubTrigger>
                            <DropdownMenuSubContent>
                              {Object.keys(SimpleStatusMap)
                                .filter((status) => !(status === "APPROVED" && order.status !== "CREATED"))
                                .map((status) => (
                                  <DropdownMenuItem
                                    key={status}
                                    onClick={async () => {
                                      const action = OutboundStatusActionMap[SimpleStatusMap[status as keyof typeof SimpleStatusMap]]
                                      if (action) await action(order)
                                    }}
                                  >
                                    {status}
                                  </DropdownMenuItem>
                                ))}
                            </DropdownMenuSubContent>
                          </DropdownMenuSub>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <PaginationControls
          currentPage={outboundPage}
          totalPages={outboundTotalPages}
          onPageChange={(page) => fetchOrders("outbound", page)}
        />
      </CardContent>
    </Card>
  )

  // ================== INBOUND TABLE ==================
  const renderInboundTable = (orders: InboundOrder[]) => (
    <Card>
      <CardHeader>
        <CardTitle>Inbound Orders</CardTitle>
        <CardDescription>Track and manage all supplier orders</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b">
                {Object.keys(InboundOrderHeaders).map((key) => (
                  <th key={key} className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">
                    {InboundOrderHeaders[key as keyof typeof InboundOrderHeaders]}
                  </th>
                ))}
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Action</th>
              </tr>
            </thead>
            <tbody>
              {orders.length === 0 ? (
                <tr>
                  <td colSpan={Object.keys(InboundOrderHeaders).length + 1} className="text-center py-12 text-muted-foreground">
                    No orders found
                  </td>
                </tr>
              ) : (
                orders.map((order) => (
                  <tr key={order.id} className="border-b hover:bg-muted/50">
                    {Object.keys(InboundOrderHeaders).map((key) => {
                      if (key === "status") {
                        const config = InboundStatusConfig[order.status]
                        return (
                          <td key={key} className="py-4 px-4">
                            <div className="flex justify-center">
                              {config ? (
                                <span className={`px-3 py-1 rounded-full text-xs font-semibold ${config.bg} ${config.text}`}>
                                  {config.label}
                                </span>
                              ) : (
                                <span className="px-3 py-1 rounded-full bg-gray-200 text-gray-600 text-xs">
                                  {order.status}
                                </span>
                              )}
                            </div>
                          </td>
                        )
                      }
                      if (key === "expectedAt") {
                        return (
                          <td key={key} className="py-4 px-4 text-sm text-muted-foreground">
                            {new Date(order.expectedAt).toLocaleDateString()}
                          </td>
                        )
                      }
                      return (
                        <td key={key} className="py-4 px-4 text-sm text-muted-foreground truncate max-w-[180px]">
                          {String(order[key as keyof InboundOrder])}
                        </td>
                      )
                    })}
                    <td className="py-4 px-4">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon">
                            <MoreHorizontal className="w-5 h-5" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuLabel>Actions</DropdownMenuLabel>
                          <DropdownMenuSeparator />
                          <DropdownMenuItem onClick={() => router.push(`/admin/orders/inbound/${order.id}`)}>
                            View Details
                          </DropdownMenuItem>
                          <DropdownMenuSub>
                            <DropdownMenuSubTrigger>Change Status</DropdownMenuSubTrigger>
                            <DropdownMenuSubContent>
                              {Object.keys(InboundStatusActionMap).map((status) => (
                                <DropdownMenuItem
                                  key={status}
                                  onClick={async () => {
                                    const action = InboundStatusActionMap[status]
                                    if (action) await action(order)
                                  }}
                                >
                                  {status}
                                </DropdownMenuItem>
                              ))}
                            </DropdownMenuSubContent>
                          </DropdownMenuSub>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <PaginationControls
          currentPage={inboundPage}
          totalPages={inboundTotalPages}
          onPageChange={(page) => fetchOrders("inbound", page)}
        />
      </CardContent>
    </Card>
  )

  // ================== MAIN RENDER ==================
  return (
    <div className="min-h-screen bg-gray-50">
      <Header title="Orders" subtitle="Monitor all fulfillment operations" />

      <div className="p-6 space-y-6">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {activeTab === "outbound" ? (
            <>
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium text-muted-foreground">Total Orders</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{outboundStats.total}</div>
                </CardContent>
              </Card>
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium text-muted-foreground">Pending</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-blue-600">{outboundStats.pending}</div>
                </CardContent>
              </Card>
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium text-muted-foreground">In Transit</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-yellow-600">{outboundStats.inTransit}</div>
                </CardContent>
              </Card>
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium text-muted-foreground">Shipped</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-green-600">{outboundStats.shipped}</div>
                </CardContent>
              </Card>
            </>
          ) : (
            <>
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium text-muted-foreground">Total Inbound</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{inboundStats.total}</div>
                </CardContent>
              </Card>
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium text-muted-foreground">Draft</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-gray-600">{inboundStats.draft}</div>
                </CardContent>
              </Card>
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium text-muted-foreground">Receiving</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-yellow-600">{inboundStats.receiving}</div>
                </CardContent>
              </Card>
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium text-muted-foreground">Received</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-green-600">{inboundStats.received}</div>
                </CardContent>
              </Card>
            </>
          )}
        </div>

        {/* Search */}
        <div className="relative max-w-md">
          <Search className="absolute left-3 top-3 w-4 h-4 text-muted-foreground" />
          <Input
            placeholder="Search orders..."
            className="pl-10"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        {/* Tabs */}
        <Tabs value={activeTab} onValueChange={(v) => setActiveTab(v as OrderType)}>
          <TabsList className="grid w-full max-w-md grid-cols-2">
            <TabsTrigger value="outbound">Outbound Orders</TabsTrigger>
            <TabsTrigger value="inbound">Inbound Orders</TabsTrigger>
          </TabsList>

          <TabsContent value="outbound" className="mt-6">
            {renderOutboundTable(outboundOrders)}
          </TabsContent>

          <TabsContent value="inbound" className="mt-6">
            {renderInboundTable(inboundOrders)}
          </TabsContent>
        </Tabs>
      </div>
    </div>
  )
}