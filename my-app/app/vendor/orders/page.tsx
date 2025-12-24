"use client"

import { useState, useEffect } from "react"
import axios from "axios"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Header } from "@/components/custom/header"
import { Search, MoreHorizontal, ArrowDown, ArrowUp, Plus, X } from "lucide-react"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { toast } from "@/hooks/use-toast"
import { OrderStatusConfig, type OrderStatus } from "@/interface/order"
import { useRouter } from "next/navigation"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { ScrollArea } from "@/components/ui/scroll-area"

const warehouses: Record<string, string> = {
  "11111111-1111-1111-1111-111111111111": "Kho Hà Nội",
  "22222222-2222-2222-2222-222222222222": "Kho Sài Gòn",
}

const InboundStatusConfig: Record<string, { label: string; bg: string; text: string }> = {
  DRAFT: { label: "Nháp", bg: "bg-gray-500/10", text: "text-gray-500" },
  CONFIRMED: { label: "Đã xác nhận", bg: "bg-blue-500/10", text: "text-blue-500" },
  RECEIVING: { label: "Đang nhận", bg: "bg-yellow-500/10", text: "text-yellow-500" },
  RECEIVED: { label: "Đã nhận", bg: "bg-green-500/10", text: "text-green-500" },
  CANCELLED: { label: "Đã hủy", bg: "bg-red-500/10", text: "text-red-500" },
}

interface InboundOrder {
  id: string
  inboundCode: string
  supplierName: string
  externalRef: string
  expectedAt: string
  status: string
  items: Array<{ productName: string; quantityExpected: number; quantityReceived: number; unitPrice: number }>
  warehouseId: string
  createdAt: string
}

interface OutboundOrder {
  id: string
  orderCode: string
  customerName: string
  customerPhone: string
  totalAmount: number
  totalItems: number
  pickedItems: number
  source: string
  status: OrderStatus
  createdAt: string
  warehouseId: string
}

interface ProductVariant {
  id: string
  sku?: string
  name: string
}

interface Product {
  id: string
  name: string
  productCode: string
  variants: ProductVariant[]
}

export default function VendorOrdersPage() {
  const [inboundOrders, setInboundOrders] = useState<InboundOrder[]>([])
  const [outboundOrders, setOutboundOrders] = useState<OutboundOrder[]>([])
  const [loadingInbound, setLoadingInbound] = useState(true)
  const [loadingOutbound, setLoadingOutbound] = useState(true)
  const [searchTerm, setSearchTerm] = useState("")
  const router = useRouter()

  // Modal & tab
  const [openCreateModal, setOpenCreateModal] = useState(false)
  const [activeTab, setActiveTab] = useState("inbound") // inbound hoặc outbound

  // Tìm kiếm sản phẩm
  const [productSearchTerm, setProductSearchTerm] = useState("")
  const [searchResults, setSearchResults] = useState<Product[]>([])
  const [searchingProduct, setSearchingProduct] = useState(false)

  // Form data chung
  const [warehouseId, setWarehouseId] = useState("11111111-1111-1111-1111-111111111111")
  const [externalRef, setExternalRef] = useState("")
  const [orderItems, setOrderItems] = useState<Array<{
    productVariantId: string
    productName: string
    quantity: number
    unitPrice: number
    notes: string
  }>>([])

  // Inbound only
  const [supplierName, setSupplierName] = useState("")
  const [expectedAt, setExpectedAt] = useState("")

  // Outbound only
  const [customerName, setCustomerName] = useState("")
  const [customerPhone, setCustomerPhone] = useState("")
  const [shippingAddress, setShippingAddress] = useState("")
  const [source, setSource] = useState("manual")

  // Fetch data
  useEffect(() => {
    async function fetchInbound() {
      try {
        const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query/my-inbound`, {
          withCredentials: true,
        })
        setInboundOrders(res.data?.data?.content || [])
      } catch (error) {
        toast({ title: "Lỗi", description: "Không tải được đơn nhập kho", variant: "destructive" })
      } finally {
        setLoadingInbound(false)
      }
    }
    fetchInbound()
  }, [])

  useEffect(() => {
    async function fetchOutbound() {
      try {
        const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query`, {
          withCredentials: true,
        })
        setOutboundOrders(res.data?.data?.content || [])
      } catch (error) {
        toast({ title: "Lỗi", description: "Không tải được đơn xuất kho", variant: "destructive" })
      } finally {
        setLoadingOutbound(false)
      }
    }
    fetchOutbound()
  }, [])

  // Reset form khi mở modal
  useEffect(() => {
    if (openCreateModal) {
      setWarehouseId("11111111-1111-1111-1111-111111111111")
      setExternalRef("")
      setOrderItems([])
      setSupplierName("")
      setExpectedAt("")
      setCustomerName("")
      setCustomerPhone("")
      setShippingAddress("")
      setSource("manual")
    }
  }, [openCreateModal])

  // Search product
  useEffect(() => {
    if (!openCreateModal || productSearchTerm.trim().length < 2) {
      setSearchResults([])
      return
    }

    const timeout = setTimeout(async () => {
      setSearchingProduct(true)
      try {
        const res = await axios.get(
          `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-query/filter?keyword=${productSearchTerm}`,
          { withCredentials: true }
        )
        setSearchResults(res.data?.data || [])
      } catch (error) {
        toast({ title: "Lỗi", description: "Không tìm được sản phẩm", variant: "destructive" })
      } finally {
        setSearchingProduct(false)
      }
    }, 500)

    return () => clearTimeout(timeout)
  }, [productSearchTerm, openCreateModal])

  const addItemToOrder = (variant: ProductVariant, productName: string, productCode: string) => {
    setOrderItems(prev => [
      ...prev,
      {
        productVariantId: variant.id,
        productName: `${productName} (${productCode}) - ${variant.name || variant.sku || ""}`.trim(),
        quantity: 1,
        unitPrice: 0,
        notes: "",
      },
    ])
    setProductSearchTerm("")
    setSearchResults([])
  }

  const removeItem = (index: number) => {
    setOrderItems(prev => prev.filter((_, i) => i !== index))
  }

  const updateItem = (index: number, field: string, value: any) => {
    setOrderItems(prev =>
      prev.map((item, i) => (i === index ? { ...item, [field]: value } : item))
    )
  }

  const createOrder = async () => {
    if (orderItems.length === 0) {
      toast({ title: "Thiếu thông tin", description: "Vui lòng thêm ít nhất 1 sản phẩm", variant: "destructive" })
      return
    }

    try {
      if (activeTab === "inbound") {
        if (!supplierName.trim()) {
          toast({ title: "Thiếu thông tin", description: "Vui lòng nhập tên nhà cung cấp", variant: "destructive" })
          return
        }
        if (!expectedAt) {
          toast({ title: "Thiếu thông tin", description: "Vui lòng chọn ngày dự kiến nhận hàng", variant: "destructive" })
          return
        }

        await axios.post(
          `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/inbound`,
          {
            warehouseId,
            externalRef: externalRef || "",
            supplierName,
            expectedAt,
            items: orderItems.map(item => ({
              productVariantId: item.productVariantId,
              productName: item.productName,
              quantityExpected: item.quantity,
              unitPrice: item.unitPrice,
              notes: item.notes,
            })),
          },
          { withCredentials: true }
        )

        toast({ title: "Thành công", description: "Tạo đơn nhập kho thành công" })

        // Reload inbound
        const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query/my-inbound`, { withCredentials: true })
        setInboundOrders(res.data?.data?.content || [])
      } else {
        if (!customerName.trim() || !customerPhone.trim()) {
          toast({ title: "Thiếu thông tin", description: "Vui lòng nhập tên và số điện thoại khách hàng", variant: "destructive" })
          return
        }

        await axios.post(
          `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command`,
          {
            customerName,
            customerPhone,
            shippingAddress: shippingAddress || "",
            externalRef: externalRef || "",
            source,
            warehouseId,
            items: orderItems.map(item => ({
              productVariantId: item.productVariantId,
              quantity: item.quantity,
              unitPrice: item.unitPrice,
              notes: item.notes,
              productName: item.productName,
            })),
          },
          { withCredentials: true }
        )

        toast({ title: "Thành công", description: "Tạo đơn xuất kho thành công" })

        // Reload outbound
        const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query`, { withCredentials: true })
        setOutboundOrders(res.data?.data?.content || [])
      }

      setOpenCreateModal(false)
    } catch (error: any) {
      toast({
        title: "Lỗi",
        description: error.response?.data?.message || "Không thể tạo đơn",
        variant: "destructive",
      })
    }
  }

  const filteredInbound = inboundOrders.filter(
    o =>
      o.inboundCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
      o.supplierName.toLowerCase().includes(searchTerm.toLowerCase())
  )

  const filteredOutbound = outboundOrders.filter(
    o =>
      o.orderCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
      o.customerName.toLowerCase().includes(searchTerm.toLowerCase())
  )

  const InboundTable = () => (
    <div className="overflow-x-auto">
      <table className="w-full text-sm">
        <thead className="border-b">
          <tr>
            <th className="text-left py-3 px-4 font-medium">Mã nhập</th>
            <th className="text-left py-3 px-4 font-medium">Nhà cung cấp</th>
            <th className="text-left py-3 px-4 font-medium">Kho</th>
            <th className="text-center py-3 px-4 font-medium">Số món</th>
            <th className="text-left py-3 px-4 font-medium">Ngày dự kiến</th>
            <th className="text-left py-3 px-4 font-medium">Trạng thái</th>
            <th className="text-left py-3 px-4 font-medium">Hành động</th>
          </tr>
        </thead>
        <tbody>
          {loadingInbound ? (
            <tr><td colSpan={7} className="text-center py-8 text-muted-foreground">Đang tải...</td></tr>
          ) : filteredInbound.length === 0 ? (
            <tr><td colSpan={7} className="text-center py-8 text-muted-foreground">Không có đơn nhập kho</td></tr>
          ) : (
            filteredInbound.map(order => (
              <tr key={order.id} className="border-b hover:bg-muted/50">
                <td className="py-4 px-4 font-mono text-primary">{order.inboundCode}</td>
                <td className="py-4 px-4">{order.supplierName}</td>
                <td className="py-4 px-4 text-muted-foreground">{warehouses[order.warehouseId] || "Kho khác"}</td>
                <td className="py-4 px-4 text-center">{order.items?.length || 0}</td>
                <td className="py-4 px-4 text-sm">
                  {order.expectedAt ? new Date(order.expectedAt).toLocaleDateString("vi-VN") : "-"}
                </td>
                <td className="py-4 px-4">
                  <Badge className={`${InboundStatusConfig[order.status]?.bg || "bg-gray-500/10"} ${InboundStatusConfig[order.status]?.text || "text-gray-500"} border-0`}>
                    {InboundStatusConfig[order.status]?.label || order.status}
                  </Badge>
                </td>
                <td className="py-4 px-4">
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <MoreHorizontal className="cursor-pointer" size={20} />
                    </DropdownMenuTrigger>
                    <DropdownMenuContent>
                      <DropdownMenuItem onClick={() => router.push(`/vendor/orders/inbound/${order.id}`)}>
                        View
                      </DropdownMenuItem>
                      <DropdownMenuItem>Cancel Order</DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  )

  const OutboundTable = () => (
    <div className="overflow-x-auto">
      <table className="w-full text-sm">
        <thead className="border-b">
          <tr>
            <th className="text-left py-3 px-4 font-medium">Mã đơn</th>
            <th className="text-left py-3 px-4 font-medium">Khách hàng</th>
            <th className="text-left py-3 px-4 font-medium">SĐT</th>
            <th className="text-left py-3 px-4 font-medium">Kho</th>
            <th className="text-center py-3 px-4 font-medium">Món / Đã lấy</th>
            <th className="text-right py-3 px-4 font-medium">Tổng tiền</th>
            <th className="text-left py-3 px-4 font-medium">Nguồn</th>
            <th className="text-left py-3 px-4 font-medium">Trạng thái</th>
            <th className="text-left py-3 px-4 font-medium">Ngày tạo</th>
            <th className="text-left py-3 px-4 font-medium">Hành động</th>
          </tr>
        </thead>
        <tbody>
          {loadingOutbound ? (
            <tr><td colSpan={10} className="text-center py-8 text-muted-foreground">Đang tải...</td></tr>
          ) : filteredOutbound.length === 0 ? (
            <tr><td colSpan={10} className="text-center py-8 text-muted-foreground">Không có đơn xuất kho</td></tr>
          ) : (
            filteredOutbound.map(order => (
              <tr key={order.id} className="border-b hover:bg-muted/50">
                <td className="py-4 px-4 font-mono text-primary">{order.orderCode}</td>
                <td className="py-4 px-4">{order.customerName}</td>
                <td className="py-4 px-4">{order.customerPhone}</td>
                <td className="py-4 px-4 text-muted-foreground">{warehouses[order.warehouseId] || "Kho khác"}</td>
                <td className="py-4 px-4 text-center">{order.totalItems} / <span className="font-medium">{order.pickedItems}</span></td>
                <td className="py-4 px-4 text-right font-semibold">{order.totalAmount}</td>
                <td className="py-4 px-4"><Badge variant="outline">{order.source}</Badge></td>
                <td className="py-4 px-4">
                  <Badge className={`${OrderStatusConfig[order.status]?.bg || "bg-gray-500/10"} ${OrderStatusConfig[order.status]?.text || "text-gray-500"} border-0`}>
                    {OrderStatusConfig[order.status]?.label || order.status}
                  </Badge>
                </td>
                <td className="py-4 px-4 text-sm text-muted-foreground">{new Date(order.createdAt).toLocaleString("vi-VN")}</td>
                <td className="py-4 px-4">
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <MoreHorizontal className="cursor-pointer" size={20} />
                    </DropdownMenuTrigger>
                    <DropdownMenuContent>
                      <DropdownMenuItem onClick={() => router.push(`/vendor/orders/outbound/${order.id}`)}>
                        View
                      </DropdownMenuItem>
                      <DropdownMenuItem>Cancel Order</DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  )

  return (
    <div className="w-full">
      <Header title="Đơn hàng" subtitle="Quản lý đơn nhập và xuất kho" />

      <div className="p-6 space-y-6">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Đơn nhập kho</CardTitle>
            </CardHeader>
            <CardContent className="flex items-center justify-between">
              <div className="text-2xl font-bold">{inboundOrders.length}</div>
              <ArrowDown className="h-8 w-8 text-primary opacity-20" />
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Đơn xuất kho</CardTitle>
            </CardHeader>
            <CardContent className="flex items-center justify-between">
              <div className="text-2xl font-bold">{outboundOrders.length}</div>
              <ArrowUp className="h-8 w-8 text-accent opacity-20" />
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Đang xử lý (nhập)</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-yellow-600">
                {inboundOrders.filter(o => o.status === "DRAFT" || o.status === "RECEIVING").length}
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Đã hoàn thành</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-green-600">
                {inboundOrders.filter(o => o.status === "RECEIVED").length +
                  outboundOrders.filter(o => o.status === "SHIPPED").length}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Search + Create Button */}
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div className="relative w-full sm:w-96">
            <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Tìm kiếm đơn hàng..."
              className="pl-10"
              value={searchTerm}
              onChange={e => setSearchTerm(e.target.value)}
            />
          </div>

          <Dialog open={openCreateModal} onOpenChange={setOpenCreateModal}>
            <DialogTrigger asChild>
              <Button>
                <Plus className="mr-2 h-4 w-4" />
                {activeTab === "inbound" ? "Tạo đơn nhập kho mới" : "Tạo đơn xuất kho mới"}
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-4xl max-h-[90vh] overflow-hidden flex flex-col">
              <DialogHeader>
                <DialogTitle>
                  {activeTab === "inbound" ? "Tạo đơn nhập kho mới" : "Tạo đơn xuất kho mới"}
                </DialogTitle>
                <DialogDescription>
                  {activeTab === "inbound"
                    ? "Nhập thông tin nhà cung cấp và danh sách sản phẩm cần nhập"
                    : "Nhập thông tin khách hàng và danh sách sản phẩm cần xuất"}
                </DialogDescription>
              </DialogHeader>

              <ScrollArea className="flex-1 pr-4">
                {/* Thông tin chung & riêng theo tab */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 py-4">
                  {activeTab === "inbound" ? (
                    <>
                      <div className="space-y-2">
                        <Label>Nhà cung cấp *</Label>
                        <Input value={supplierName} onChange={e => setSupplierName(e.target.value)} placeholder="Tên nhà cung cấp" />
                      </div>
                      <div className="space-y-2">
                        <Label>Ngày dự kiến nhận *</Label>
                        <Input type="date" value={expectedAt} onChange={e => setExpectedAt(e.target.value)} />
                      </div>
                    </>
                  ) : (
                    <>
                      <div className="space-y-2">
                        <Label>Họ tên khách hàng *</Label>
                        <Input value={customerName} onChange={e => setCustomerName(e.target.value)} />
                      </div>
                      <div className="space-y-2">
                        <Label>Số điện thoại *</Label>
                        <Input value={customerPhone} onChange={e => setCustomerPhone(e.target.value)} />
                      </div>
                      <div className="space-y-2 md:col-span-2">
                        <Label>Địa chỉ giao hàng</Label>
                        <Textarea value={shippingAddress} onChange={e => setShippingAddress(e.target.value)} />
                      </div>
                      <div className="space-y-2">
                        <Label>Nguồn đơn</Label>
                        <Input value={source} onChange={e => setSource(e.target.value)} placeholder="manual, shopee, lazada..." />
                      </div>
                    </>
                  )}

                  <div className="space-y-2">
                    <Label>Mã tham chiếu (external ref)</Label>
                    <Input value={externalRef} onChange={e => setExternalRef(e.target.value)} />
                  </div>

                  <div className="space-y-2 md:col-span-2">
                    <Label>Kho</Label>
                    <select
                      className="w-full h-10 px-3 rounded-md border border-input bg-background"
                      value={warehouseId}
                      onChange={e => setWarehouseId(e.target.value)}
                    >
                      <option value="11111111-1111-1111-1111-111111111111">Kho Hà Nội</option>
                      <option value="22222222-2222-2222-2222-222222222222">Kho Sài Gòn</option>
                    </select>
                  </div>
                </div>

                {/* Tìm kiếm và danh sách sản phẩm */}
                <div className="space-y-4 mt-6">
                  <div className="space-y-2">
                    <Label>Tìm kiếm sản phẩm (mã, SKU, tên)</Label>
                    <div className="relative">
                      <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                      <Input
                        className="pl-10"
                        placeholder="Nhập ít nhất 2 ký tự..."
                        value={productSearchTerm}
                        onChange={e => setProductSearchTerm(e.target.value)}
                      />
                    </div>

                    {searchingProduct && <p className="text-sm text-muted-foreground">Đang tìm...</p>}

                    {searchResults.length > 0 && (
                      <div className="border rounded-md max-h-60 overflow-y-auto">
                        {searchResults.map(product => (
                          <div key={product.id} className="border-b last:border-b-0">
                            <div className="p-3 font-medium">
                              {product.productCode} - {product.name}
                            </div>
                            {product.variants.map(variant => (
                              <div
                                key={variant.id}
                                className="px-6 py-2 hover:bg-muted cursor-pointer flex justify-between items-center"
                                onClick={() => addItemToOrder(variant, product.name, product.productCode)}
                              >
                                <span className="text-sm">
                                  {variant.sku && <span className="font-mono mr-2">{variant.sku}</span>}
                                  {variant.name || "Variant mặc định"}
                                </span>
                                <Plus className="h-4 w-4 text-primary" />
                              </div>
                            ))}
                          </div>
                        ))}
                      </div>
                    )}
                  </div>

                  <div className="space-y-2">
                    <Label>Danh sách sản phẩm ({orderItems.length})</Label>
                    {orderItems.length === 0 ? (
                      <p className="text-sm text-muted-foreground py-8 text-center">Chưa có sản phẩm nào</p>
                    ) : (
                      <div className="space-y-3">
                        {orderItems.map((item, index) => (
                          <div key={index} className="border rounded-lg p-4 space-y-3">
                            <div className="flex justify-between items-start">
                              <p className="font-medium">{item.productName}</p>
                              <Button variant="ghost" size="sm" onClick={() => removeItem(index)}>
                                <X className="h-4 w-4" />
                              </Button>
                            </div>
                            <div className="grid grid-cols-3 gap-3">
                              <div>
                                <Label className="text-xs">Số lượng</Label>
                                <Input
                                  type="number"
                                  min="1"
                                  value={item.quantity}
                                  onChange={e => updateItem(index, "quantity", Number(e.target.value) || 1)}
                                />
                              </div>
                              <div>
                                <Label className="text-xs">Đơn giá (giá vốn)</Label>
                                <Input
                                  type="number"
                                  min="0"
                                  value={item.unitPrice}
                                  onChange={e => updateItem(index, "unitPrice", Number(e.target.value) || 0)}
                                />
                              </div>
                              <div>
                                <Label className="text-xs">Ghi chú</Label>
                                <Input
                                  value={item.notes}
                                  onChange={e => updateItem(index, "notes", e.target.value)}
                                />
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              </ScrollArea>

              <DialogFooter>
                <Button variant="outline" onClick={() => setOpenCreateModal(false)}>Hủy</Button>
                <Button onClick={createOrder}>Tạo đơn</Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>

        {/* Tabs */}
        <Tabs defaultValue="inbound" onValueChange={setActiveTab}>
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="outbound">Đơn xuất kho ({outboundOrders.length})</TabsTrigger>
            <TabsTrigger value="inbound">Đơn nhập kho ({inboundOrders.length})</TabsTrigger>
          </TabsList>

          <TabsContent value="outbound" className="mt-6">
            <Card>
              <CardHeader>
                <CardTitle>Đơn xuất kho</CardTitle>
              </CardHeader>
              <CardContent>
                <OutboundTable />
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="inbound" className="mt-6">
            <Card>
              <CardHeader>
                <CardTitle>Đơn nhập kho</CardTitle>
              </CardHeader>
              <CardContent>
                <InboundTable />
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  )
}