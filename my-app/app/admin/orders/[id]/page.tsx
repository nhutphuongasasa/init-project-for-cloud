'use client'

import { Header } from '@/components/custom/header'
import { Card, CardContent, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { FulfillmentOrders, OrderStatusConfig, NextStatusMap, OrderStatus } from '@/interface/order'
import api from '@/lib/axios'
import { useParams, useRouter } from 'next/navigation'
import { useEffect, useState } from 'react'
import { Minus, Plus, PackageCheck, PackageOpen, Truck, XCircle, RotateCcw } from 'lucide-react'
import { useToast } from '@/hooks/use-toast'

const OrderDetails = () => {
  const params = useParams()
  const router = useRouter()
  const orderId = params.id as string
  const { toast } = useToast()

  const [fulfillmentOrder, setFulfillmentOrder] = useState<FulfillmentOrders>()
  const [quantities, setQuantities] = useState<Record<number, number>>({})
  
  //fetch fulfillmentorder
  const fetchFulfillmentOrder = async () => {
    try {
      const res = await api.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query/${orderId}`, {
        withCredentials: true
      });

      console.log(res.data)

      setFulfillmentOrder(res.data.data)

      const initialQuantities: Record<number, number> = {}
      res.data.data.items.forEach((item: any) => {
        initialQuantities[item.id] = item.quantityPicked || 0
      })
      setQuantities(initialQuantities)
    } catch (error) {
      console.error('Error fetching order:', error)
      toast({ title: "Failed to fetch order", variant: "destructive" })
    }
  };

  useEffect(() => {
    fetchFulfillmentOrder();
  }, []);

  const handleQuantityChange = (itemId: number, value: string) => {
    const numValue = parseInt(value) || 0
    setQuantities(prev => ({
      ...prev,
      [itemId]: numValue
    }))
  }

  const handleIncrement = (itemId: number, maxQuantity: number) => {
    setQuantities(prev => ({
      ...prev,
      [itemId]: Math.min((prev[itemId] || 0) + 1, maxQuantity)
    }))
  }

  const handleDecrement = (itemId: number) => {
    setQuantities(prev => ({
      ...prev,
      [itemId]: Math.max((prev[itemId] || 0) - 1, 0)
    }))
  }

  const handleUpdateQuantity = async (itemId: number) => {
    try {
      const res = await api.put(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${itemId}/update-picked-quantity`, 
        { quantityPick: quantities[itemId] },
        { withCredentials: true }
      );

      toast({ title: "Quantity updated successfully" })
      await fetchFulfillmentOrder()
    } catch (error) {
      console.error('Error updating quantity:', error)
      toast({ title: "Failed to update quantity", variant: "destructive" })
    }
  }

  // ================== ORDER STATUS ACTIONS ==================

  const handleStartPicking = async () => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${orderId}/start-picking`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Picking completed successfully" })
      await fetchFulfillmentOrder()
    } catch (error) {
      console.error('Error completing picking:', error)
      toast({ title: "Failed to complete picking", variant: "destructive" })
    }
  }

  const handleCompletePicking = async () => {
    try {
      // Chuẩn bị dữ liệu picking items từ state quantities và fulfillmentOrder items
      const items = fulfillmentOrder?.items.map(item => ({
        detailId: item.id,
        quantityPicked: quantities[item.id] || 0,
        notes: item.notes || ""
      })) || []

      const res = await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${orderId}/complete-picking`,
        {
          items: items
        },
        { withCredentials: true }
      )
      console.log(res)
      toast({ title: "Picking completed successfully" })
      await fetchFulfillmentOrder()
    } catch (error) {
      console.error('Error completing picking:', error)
      toast({ title: "Failed to complete picking", variant: "destructive" })
    }
  }

  const handleStartPacking = async () => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${orderId}/start-packing`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Packing started successfully" })
      await fetchFulfillmentOrder()
    } catch (error) {
      console.error('Error starting packing:', error)
      toast({ title: "Failed to start packing", variant: "destructive" })
    }
  }

  const handleCompletePacking = async () => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${orderId}/complete-packing`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Packing completed successfully" })
      await fetchFulfillmentOrder()
    } catch (error) {
      console.error('Error completing packing:', error)
      toast({ title: "Failed to complete packing", variant: "destructive" })
    }
  }

  const handleShipOrder = async () => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/ship`,
        {
          orderId: orderId,
          vendorId: fulfillmentOrder?.vendorId
        },
        { withCredentials: true }
      )
      toast({ title: "Order shipped successfully" })
      await fetchFulfillmentOrder()
    } catch (error) {
      console.error('Error shipping order:', error)
      toast({ title: "Failed to ship order", variant: "destructive" })
    }
  }

  const handleCancelOrder = async () => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/cancel/${orderId}`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Order cancelled successfully" })
      await fetchFulfillmentOrder()
    } catch (error) {
      console.error('Error cancelling order:', error)
      toast({ title: "Failed to cancel order", variant: "destructive" })
    }
  }

  const handleReturnOrder = async () => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/return/${orderId}`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Order returned successfully" })
      await fetchFulfillmentOrder()
    } catch (error) {
      console.error('Error returning order:', error)
      toast({ title: "Failed to return order", variant: "destructive" })
    }
  }

  // ================== ACTION BUTTON CONFIG ==================
  const getActionButtons = (currentStatus: OrderStatus) => {
    const nextStatuses = NextStatusMap[currentStatus] || []
    const buttons = []

    if (nextStatuses.includes('PICKING')) {
      buttons.push({
        label: 'Start Picking',
        onClick: handleStartPicking,
        icon: PackageCheck,
        variant: 'default' as const,
        className: 'bg-blue-600 hover:bg-blue-700'
      })
    }

    if (nextStatuses.includes('PICKED')) {
      buttons.push({
        label: 'Complete Picking',
        onClick: handleCompletePicking,
        icon: PackageCheck,
        variant: 'default' as const,
        className: 'bg-blue-600 hover:bg-blue-700'
      })
    }

    if (nextStatuses.includes('PACKING')) {
      buttons.push({
        label: 'Start Packing',
        onClick: handleStartPacking,
        icon: PackageOpen,
        variant: 'default' as const,
        className: 'bg-purple-600 hover:bg-purple-700'
      })
    }

    if (nextStatuses.includes('PACKED')) {
      buttons.push({
        label: 'Complete Packing',
        onClick: handleCompletePacking,
        icon: PackageCheck,
        variant: 'default' as const,
        className: 'bg-indigo-600 hover:bg-indigo-700'
      })
    }

    if (nextStatuses.includes('SHIPPED')) {
      buttons.push({
        label: 'Ship Order',
        onClick: handleShipOrder,
        icon: Truck,
        variant: 'default' as const,
        className: 'bg-green-600 hover:bg-green-700'
      })
    }

    if (nextStatuses.includes('CANCELLED')) {
      buttons.push({
        label: 'Cancel Order',
        onClick: handleCancelOrder,
        icon: XCircle,
        variant: 'destructive' as const,
        className: ''
      })
    }

    if (nextStatuses.includes('RETURNED')) {
      buttons.push({
        label: 'Return Order',
        onClick: handleReturnOrder,
        icon: RotateCcw,
        variant: 'outline' as const,
        className: 'border-orange-500 text-orange-500 hover:bg-orange-50'
      })
    }

    return buttons
  }

  if (!fulfillmentOrder) {
    return <div className="bg-gray-100 h-full w-full flex items-center justify-center">Loading...</div>
  }

  const statusConfig = OrderStatusConfig[fulfillmentOrder.status]
  const actionButtons = getActionButtons(fulfillmentOrder.status.toUpperCase() as OrderStatus)

  return (
    <div className='bg-gray-100 min-h-screen w-full'>
      <Header title="Pick Order" subtitle={`Manage the picking process for order`} />
      
      <div className='flex flex-col p-4 gap-4'>
        {/* Basic Info Card */}
        <Card className='w-full'>
          <CardContent className='pt-6'>
            <div className='flex justify-between items-start mb-4'>
              <CardTitle className='text-lg font-semibold'>Basic Info</CardTitle>
              
              {/* Action Buttons */}
              {actionButtons.length > 0 && (
                <div className='flex gap-2 flex-wrap'>
                  {actionButtons.map((button, index) => {
                    const Icon = button.icon
                    return (
                      <Button
                        key={index}
                        onClick={button.onClick}
                        variant={button.variant}
                        className={button.className}
                        size="sm"
                      >
                        <Icon className="h-4 w-4 mr-2" />
                        {button.label}
                      </Button>
                    )
                  })}
                </div>
              )}
            </div>
            
            <div className='grid grid-cols-1 md:grid-cols-2 gap-4'>
              <div>
                <p className='text-sm text-gray-500'>Order Code</p>
                <p className='font-medium'>{fulfillmentOrder.orderCode}</p>
              </div>
              
              <div>
                <p className='text-sm text-gray-500'>Status</p>
                <Badge className={`${statusConfig.bg} ${statusConfig.text} hover:${statusConfig.bg}`}>
                  {statusConfig.label}
                </Badge>
              </div>
              
              <div>
                <p className='text-sm text-gray-500'>Customer Name</p>
                <p className='font-medium'>{fulfillmentOrder.customerName}</p>
              </div>
              
              <div>
                <p className='text-sm text-gray-500'>Customer Phone</p>
                <p className='font-medium'>{fulfillmentOrder.customerPhone}</p>
              </div>
              
              <div className='md:col-span-2'>
                <p className='text-sm text-gray-500'>Shipping Address</p>
                <p className='font-medium'>{fulfillmentOrder.shippingAddress}</p>
              </div>
              
              <div>
                <p className='text-sm text-gray-500'>External Ref</p>
                <p className='font-medium'>{fulfillmentOrder.externalRef}</p>
              </div>
              
              <div>
                <p className='text-sm text-gray-500'>Source</p>
                <p className='font-medium'>{fulfillmentOrder.source}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Items Card */}
        <Card className='w-full'>
          <CardContent className='pt-6'>
            <CardTitle className='text-lg font-semibold mb-4'>Order Items</CardTitle>
            
            <div className='space-y-4'>
              {fulfillmentOrder.items.map((item) => (
                <div 
                  key={item.id} 
                  className='border rounded-lg p-4 bg-white hover:shadow-md transition-shadow'
                >
                  <div className='flex flex-col md:flex-row md:items-center justify-between gap-4'>
                    <div className='flex-1 mr-5'>
                      <h3 className='font-semibold text-base'>{item.productName}</h3>
                      <p className='text-sm text-gray-500 mt-1 '>Product Variant ID: {item.productVariantId}</p>
                      <p className='text-sm text-gray-600 mt-1'>Unit Price: ${item.unitPrice.toFixed(2)}</p>
                      {item.notes && (
                        <p className='text-sm text-gray-500 mt-1 italic'>Notes: {item.notes}</p>
                      )}
                    </div>
                    
                    <div className='flex items-center gap-4'>
                      <div className='text-center'>
                        <p className='text-xs text-gray-500'>Requested</p>
                        <p className='text-lg font-semibold'>{item.quantityRequested}</p>
                      </div>
                      
                      <div className='flex items-center gap-2'>
                        <Button
                          variant="outline"
                          size="icon"
                          className="h-8 w-8"
                          onClick={() => handleDecrement(item.id)}
                        >
                          <Minus className="h-4 w-4" />
                        </Button>
                        
                        <Input
                          type="number"
                          min="0"
                          max={item.quantityRequested}
                          value={quantities[item.id] || 0}
                          onChange={(e) => handleQuantityChange(item.id, e.target.value)}
                          className="w-20 text-center"
                        />
                        
                        <Button
                          variant="outline"
                          size="icon"
                          className="h-8 w-8"
                          onClick={() => handleIncrement(item.id, item.quantityRequested)}
                        >
                          <Plus className="h-4 w-4" />
                        </Button>
                      </div>
                      
                      <Button 
                        onClick={() => handleUpdateQuantity(item.id)}
                        className="whitespace-nowrap"
                        size="sm"
                      >
                        Update Quantity
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

export default OrderDetails