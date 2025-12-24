'use client'

import { Header } from '@/components/custom/header'
import { Card, CardContent, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { useParams, useRouter } from 'next/navigation'
import { useEffect, useState } from 'react'
import { XCircle } from 'lucide-react'
import { useToast } from '@/hooks/use-toast'
import api from '@/lib/axios'

// ================== INTERFACES ==================
interface InboundOrderItem {
  id: number
  productVariantId: string
  productName: string
  quantityExpected: number
  quantityReceived: number
  notes?: string
  unitPrice: number
}

interface InboundOrder {
  id: string
  inboundCode: string
  supplierName: string
  externalRef: string
  expectedAt: string
  receivedAt: string | null
  status: InboundStatus
  vendorId: string
  warehouseId: string
  items: InboundOrderItem[]
  createdAt: string
  createdBy: string
}

type InboundStatus = 'DRAFT' | 'CONFIRMED' | 'RECEIVING' | 'RECEIVED' | 'CANCELLED'

// ================== STATUS CONFIG ==================
const InboundStatusConfig: Record<InboundStatus, { label: string; bg: string; text: string }> = {
  DRAFT: { label: "Draft", bg: "bg-gray-100", text: "text-gray-700" },
  CONFIRMED: { label: "Confirmed", bg: "bg-blue-100", text: "text-blue-700" },
  RECEIVING: { label: "Receiving", bg: "bg-yellow-100", text: "text-yellow-700" },
  RECEIVED: { label: "Received", bg: "bg-green-100", text: "text-green-700" },
  CANCELLED: { label: "Cancelled", bg: "bg-red-100", text: "text-red-700" },
}

const InboundOrderDetails = () => {
  const params = useParams()
  const router = useRouter()
  const inboundId = params.id as string
  const { toast } = useToast()

  const [inboundOrder, setInboundOrder] = useState<InboundOrder | null>(null)
  
  // ================== FETCH INBOUND ORDER ==================
  const fetchInboundOrder = async () => {
    try {
      const res = await api.get(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query/my-inbound/${inboundId}`, 
        { withCredentials: true }
      )

      console.log('Inbound order data:', res.data)
      setInboundOrder(res.data.data)
    } catch (error) {
      console.error('Error fetching inbound order:', error)
      toast({ title: "Failed to fetch inbound order", variant: "destructive" })
    }
  }

  useEffect(() => {
    fetchInboundOrder()
  }, [inboundId])

  // ================== CANCEL HANDLER ==================
  const handleCancelInbound = async () => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/inbound-command/${inboundId}/cancel`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Inbound order cancelled successfully" })
      await fetchInboundOrder()
    } catch (error) {
      console.error('Error cancelling inbound:', error)
      toast({ title: "Failed to cancel inbound order", variant: "destructive" })
    }
  }

  // Format currency VND
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(amount)
  }

  if (!inboundOrder) {
    return (
      <div className="bg-gray-100 h-full w-full flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading inbound order...</p>
        </div>
      </div>
    )
  }

  const statusConfig = InboundStatusConfig[inboundOrder.status]
  const canCancel = !['CANCELLED', 'RECEIVED'].includes(inboundOrder.status)

  return (
    <div className='bg-gray-100 min-h-screen w-full'>
      <Header 
        title="Inbound Order Details" 
        subtitle={`View inbound order information`} 
      />
      
      <div className='flex flex-col p-4 gap-4'>
        {/* Basic Info Card */}
        <Card className='w-full'>
          <CardContent className='pt-6'>
            <div className='flex justify-between items-start mb-4'>
              <CardTitle className='text-lg font-semibold'>Order Information</CardTitle>
              
              {/* Cancel Button */}
              {canCancel && (
                <Button
                  onClick={handleCancelInbound}
                  variant="destructive"
                  size="sm"
                >
                  <XCircle className="h-4 w-4 mr-2" />
                  Cancel Order
                </Button>
              )}
            </div>
            
            <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4'>
              <div>
                <p className='text-sm text-gray-500'>Inbound Code</p>
                <p className='font-medium'>{inboundOrder.inboundCode}</p>
              </div>
              
              <div>
                <p className='text-sm text-gray-500'>Status</p>
                <Badge className={`${statusConfig.bg} ${statusConfig.text} hover:${statusConfig.bg}`}>
                  {statusConfig.label}
                </Badge>
              </div>
              
              <div>
                <p className='text-sm text-gray-500'>Supplier Name</p>
                <p className='font-medium'>{inboundOrder.supplierName}</p>
              </div>
              
              <div>
                <p className='text-sm text-gray-500'>External Reference</p>
                <p className='font-medium'>{inboundOrder.externalRef}</p>
              </div>
              
              <div>
                <p className='text-sm text-gray-500'>Expected Date</p>
                <p className='font-medium'>
                  {new Date(inboundOrder.expectedAt).toLocaleDateString('vi-VN', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric'
                  })}
                </p>
              </div>
              
              <div>
                <p className='text-sm text-gray-500'>Received Date</p>
                <p className='font-medium'>
                  {inboundOrder.receivedAt 
                    ? new Date(inboundOrder.receivedAt).toLocaleDateString('vi-VN', {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                      })
                    : <span className='text-gray-400 italic'>Not received yet</span>
                  }
                </p>
              </div>

              <div>
                <p className='text-sm text-gray-500'>Created Date</p>
                <p className='font-medium'>
                  {new Date(inboundOrder.createdAt).toLocaleDateString('vi-VN', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                  })}
                </p>
              </div>

              <div>
                <p className='text-sm text-gray-500'>Warehouse ID</p>
                <p className='font-mono text-xs text-gray-600'>{inboundOrder.warehouseId}</p>
              </div>

              <div>
                <p className='text-sm text-gray-500'>Total Items</p>
                <p className='font-bold text-lg text-blue-600'>{inboundOrder.items.length}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Items Card */}
        <Card className='w-full'>
          <CardContent className='pt-6'>
            <CardTitle className='text-lg font-semibold mb-4'>
              Order Items ({inboundOrder.items.length} items)
            </CardTitle>
            
            <div className='space-y-4'>
              {inboundOrder.items.map((item) => (
                <div 
                  key={item.id} 
                  className='border rounded-lg p-4 bg-white hover:shadow-md transition-shadow'
                >
                  <div className='flex flex-col gap-2'>
                    <div className='flex-1'>
                      <h3 className='font-semibold text-base text-gray-800'>
                        {item.productName}
                      </h3>
                      <p className='text-sm text-gray-500 mt-1'>
                        Variant ID: <span className='font-mono text-xs'>{item.productVariantId}</span>
                      </p>
                      <p className='text-sm font-medium text-green-600 mt-1'>
                        Unit Price: {formatCurrency(item.unitPrice)}
                      </p>
                      <p className='text-sm text-gray-600 mt-1'>
                        Quantity Expected: <span className='font-semibold'>{item.quantityExpected}</span>
                      </p>
                      <p className='text-sm text-gray-600 mt-1'>
                        Quantity Received: <span className='font-semibold'>{item.quantityReceived}</span>
                      </p>
                      {item.notes && (
                        <p className='text-sm text-gray-500 mt-2 italic bg-blue-50 px-3 py-2 rounded-md border border-blue-100'>
                          <span className='font-medium'>Notes:</span> {item.notes}
                        </p>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Summary */}
            <div className='mt-6 pt-6 border-t'>
              <div className='grid grid-cols-2 md:grid-cols-3 gap-4'>
                <div className='bg-gray-50 p-4 rounded-lg text-center'>
                  <p className='text-sm text-gray-600 mb-1'>Total Items</p>
                  <p className='text-2xl font-bold text-gray-700'>
                    {inboundOrder.items.length}
                  </p>
                </div>
                <div className='bg-blue-50 p-4 rounded-lg text-center'>
                  <p className='text-sm text-blue-600 mb-1'>Total Expected</p>
                  <p className='text-2xl font-bold text-blue-700'>
                    {inboundOrder.items.reduce((sum, item) => sum + item.quantityExpected, 0)}
                  </p>
                </div>
                <div className='bg-green-50 p-4 rounded-lg text-center'>
                  <p className='text-sm text-green-600 mb-1'>Total Received</p>
                  <p className='text-2xl font-bold text-green-700'>
                    {inboundOrder.items.reduce((sum, item) => sum + item.quantityReceived, 0)}
                  </p>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

export default InboundOrderDetails