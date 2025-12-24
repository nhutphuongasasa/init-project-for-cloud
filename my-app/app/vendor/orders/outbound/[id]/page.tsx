'use client'

import { Header } from '@/components/custom/header'
import { Card, CardContent, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { FulfillmentOrders, OrderStatusConfig, OrderStatus } from '@/interface/order'
// import api from '@/lib/axios'
import { useParams, useRouter } from 'next/navigation'
import { useEffect, useState } from 'react'
import { XCircle } from 'lucide-react'
import { useToast } from '@/hooks/use-toast'
import api from '@/lib/axios'

const OrderDetails = () => {
  const params = useParams()
  const router = useRouter()
  const orderId = params.id as string
  const { toast } = useToast()

  const [fulfillmentOrder, setFulfillmentOrder] = useState<FulfillmentOrders>()
  
  //fetch fulfillmentorder
  const fetchFulfillmentOrder = async () => {
    try {
      const res = await api.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query/${orderId}`, {
        withCredentials: true
      });

      console.log(res.data)

      setFulfillmentOrder(res.data.data)
    } catch (error) {
      console.error('Error fetching order:', error)
      toast({ title: "Failed to fetch order", variant: "destructive" })
    }
  };

  useEffect(() => {
    fetchFulfillmentOrder();
  }, []);

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

  if (!fulfillmentOrder) {
    return <div className="bg-gray-100 h-full w-full flex items-center justify-center">Loading...</div>
  }

  const statusConfig = OrderStatusConfig[fulfillmentOrder.status]
  const canCancel = !['CANCELLED', 'SHIPPED', 'RETURNED'].includes(fulfillmentOrder.status.toUpperCase())

  return (
    <div className='bg-gray-100 min-h-screen w-full'>
      <Header title="Order Details" subtitle={`View order information`} />
      
      <div className='flex flex-col p-4 gap-4'>
        {/* Basic Info Card */}
        <Card className='w-full'>
          <CardContent className='pt-6'>
            <div className='flex justify-between items-start mb-4'>
              <CardTitle className='text-lg font-semibold'>Basic Info</CardTitle>
              
              {/* Cancel Button */}
              {canCancel && (
                <Button
                  onClick={handleCancelOrder}
                  variant="destructive"
                  size="sm"
                >
                  <XCircle className="h-4 w-4 mr-2" />
                  Cancel Order
                </Button>
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
                  <div className='flex flex-col gap-2'>
                    <div className='flex-1'>
                      <h3 className='font-semibold text-base'>{item.productName}</h3>
                      <p className='text-sm text-gray-500 mt-1'>Product Variant ID: {item.productVariantId}</p>
                      <p className='text-sm text-gray-600 mt-1'>Unit Price: ${item.unitPrice.toFixed(2)}</p>
                      <p className='text-sm text-gray-600 mt-1'>Quantity Requested: {item.quantityRequested}</p>
                      {item.notes && (
                        <p className='text-sm text-gray-500 mt-1 italic'>Notes: {item.notes}</p>
                      )}
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