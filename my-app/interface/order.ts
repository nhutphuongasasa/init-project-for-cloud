
export type OrderStatus = "CREATED" | "APPROVED" | "PICKING" | "PICKED" | "PACKING" | "PACKED" | "SHIPPED" | "CANCELLED" | "RETURNED"

export interface Order {
    id: string;
    orderCode: string;
    customerName: string;
    customerPhone: string;
    totalAmount: number;
    totalItems: number;
    vendorId: string;
    pickedItems: number;
    source: string;
    status: OrderStatus;
    createdAt: string;
    warehouseId: string;
}

export const OrderHeaders: Record<keyof Order, string> = {
  id: "ID",
  orderCode: "OrderCode",
  customerName: "CustomerName",
  customerPhone: "CustomerPhone",
  totalAmount: "TotalAmount",
  totalItems: "TotalItems",
  vendorId: "VendorId",
  pickedItems: "PickedItems",
  source: "Source",
  status: "Status",
  createdAt: "CreatedAt",
  warehouseId: "Warehouse",
}

export const OrderStatusConfig: Record<OrderStatus, { 
    bg: string; 
    text: string; 
    label: string 
}> = {
  CREATED:   { bg: "bg-green-500/10", text: "text-green-500", label: "CREATED" },
  APPROVED:    { bg: "bg-yellow-500/10", text: "text-yellow-500", label: "APPROVED" },
  PICKING: { bg: "bg-gray-500/10", text: "text-gray-500", label: "PICKING" },
  PICKED:   { bg: "bg-red-500/10", text: "text-red-500", label: "PICKED" },
  PACKING:   { bg: "bg-red-500/10", text: "text-red-500", label: "PACKING" },
  PACKED:   { bg: "bg-red-500/10", text: "text-red-500", label: "PACKED" },
  SHIPPED:   { bg: "bg-red-500/10", text: "text-red-500", label: "SHIPPED" },
  CANCELLED:   { bg: "bg-red-500/10", text: "text-red-500", label: "CANCELLED" },
  RETURNED:   { bg: "bg-red-500/10", text: "text-red-500", label: "RETURNED" },
}

export const NextStatusMap: Record<OrderStatus, OrderStatus[]> = {
  CREATED: ["APPROVED", "CANCELLED", "RETURNED"],
  APPROVED: ["PICKING", "CANCELLED", "RETURNED"],
  PICKING: ["PICKED", "CANCELLED", "RETURNED"],
  PICKED: ["PACKING", "CANCELLED", "RETURNED"],
  PACKING: ["PACKED", "CANCELLED", "RETURNED"],
  PACKED: ["SHIPPED", "CANCELLED", "RETURNED"],
  SHIPPED: ["RETURNED"],
  CANCELLED: [],
  RETURNED: [],
}



export const SimpleStatusMap: Record<string, string> = {
  APPROVED: "APPROVED",
  CANCELLED: "CANCELLED",
  RETURNED: "RETURNED",
}

export interface FulfillmentOrders {
  id: string;
  orderCode: string;
  vendorId: string;
  warehouseId: string;
  externalRef: string;
  customerName: string;
  customerPhone: string;
  shippingAddress: string;
  source: string;
  status: OrderStatus;
  pickedAt: string;
  packedAt: string;
  shippedAt: string;
  cancelledAt: string;
  createdBy: string;
  createdAt: string;
  items: FulfillmentOrderDetail[];
}

export interface FulfillmentOrderDetail {
  id: number;
  productVariantId: string;
  productName: string;
  quantityRequested: number;
  quantityPicked: number | null;
  unitPrice: number;
  notes: string;
}

export type OrderType = 'outbound' | 'inbound'

export interface InboundOrder {
  id: string
  inboundCode: string
  supplierName: string
  externalRef: string
  expectedAt: string
  receivedAt: string | null
  status: string
  vendorId: string
  warehouseId: string
  items: any[]
  createdAt: string
  createdBy: string
}

export const InboundOrderHeaders = {
  inboundCode: "Inbound Code",
  supplierName: "Supplier",
  externalRef: "External Ref",
  expectedAt: "Expected Date",
  status: "Status",
}

export const InboundStatusConfig: Record<string, { label: string; bg: string; text: string }> = {
  DRAFT: { label: "Draft", bg: "bg-gray-100", text: "text-gray-700" },
  CONFIRMED: { label: "Confirmed", bg: "bg-blue-100", text: "text-blue-700" },
  RECEIVING: { label: "Receiving", bg: "bg-yellow-100", text: "text-yellow-700" },
  RECEIVED: { label: "Received", bg: "bg-green-100", text: "text-green-700" },
  CANCELLED: { label: "Cancelled", bg: "bg-red-100", text: "text-red-700" },
}