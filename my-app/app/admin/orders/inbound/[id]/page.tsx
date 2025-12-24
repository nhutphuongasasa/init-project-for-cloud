'use client'

import { Header } from '@/components/custom/header'
import { Card, CardContent, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import api from '@/lib/axios'
import { useParams, useRouter } from 'next/navigation'
import { useEffect, useState } from 'react'
import { Minus, Plus, PackageCheck, PackageOpen, XCircle, CheckCircle } from 'lucide-react'
import { useToast } from '@/hooks/use-toast'

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

// Status transition map
const NextStatusMap: Record<InboundStatus, InboundStatus[]> = {
  DRAFT: ['CONFIRMED', 'CANCELLED'],
  CONFIRMED: ['RECEIVING', 'CANCELLED'],
  RECEIVING: ['RECEIVED'],
  RECEIVED: [],
  CANCELLED: [],
}

const InboundOrderDetails = () => {
  const params = useParams()
  const router = useRouter()
  const inboundId = params.id as string
  const { toast } = useToast()

  const [inboundOrder, setInboundOrder] = useState<InboundOrder | null>(null)
  const [quantities, setQuantities] = useState<Record<number, number>>({})
  const [notes, setNotes] = useState<Record<number, string>>({})
  
  // ================== FETCH INBOUND ORDER ==================
  const fetchInboundOrder = async () => {
    try {
      const res = await api.get(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query/inbound/${inboundId}`, 
        { withCredentials: true }
      )

      console.log('Inbound order data:', res.data)

      const data = res.data.data
      setInboundOrder(data)

      // Initialize quantities and notes
      const initialQuantities: Record<number, number> = {}
      const initialNotes: Record<number, string> = {}
      
      data.items.forEach((item: InboundOrderItem) => {
        initialQuantities[item.id] = item.quantityReceived || 0
        initialNotes[item.id] = item.notes || ''
      })
      
      setQuantities(initialQuantities)
      setNotes(initialNotes)
    } catch (error) {
      console.error('Error fetching inbound order:', error)
      toast({ title: "Failed to fetch inbound order", variant: "destructive" })
    }
  }

  useEffect(() => {
    fetchInboundOrder()
  }, [inboundId])

  // ================== QUANTITY HANDLERS ==================
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
      [itemId]: Math.min((prev[itemId] || 0) + 1, maxQuantity + 10) // Allow slight overreceiving
    }))
  }

  const handleDecrement = (itemId: number) => {
    setQuantities(prev => ({
      ...prev,
      [itemId]: Math.max((prev[itemId] || 0) - 1, 0)
    }))
  }

  const handleNotesChange = (itemId: number, value: string) => {
    setNotes(prev => ({
      ...prev,
      [itemId]: value
    }))
  }

  const handleUpdateQuantity = async (itemId: number) => {
    try {
      await api.patch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${itemId}`,
        {
          quantityReceived: quantities[itemId] || 0,
        },
        { withCredentials: true }
      )
      toast({ title: "Quantity updated successfully" })
      await fetchInboundOrder()
    } catch (error) {
      console.error('Error updating quantity:', error)
      toast({ title: "Failed to update quantity", variant: "destructive" })
    }
  }

  // ================== STATUS ACTION HANDLERS ==================
  const handleConfirmInbound = async () => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${inboundId}/confirm-inbound`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Inbound order confirmed successfully" })
      await fetchInboundOrder()
    } catch (error) {
      console.error('Error confirming inbound:', error)
      toast({ title: "Failed to confirm inbound order", variant: "destructive" })
    }
  }

  const handleStartReceiving = async () => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${inboundId}/start-receiving`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Started receiving goods" })
      await fetchInboundOrder()
    } catch (error) {
      console.error('Error starting receiving:', error)
      toast({ title: "Failed to start receiving", variant: "destructive" })
    }
  }

  const handleCompleteReceiving = async () => {
    try {
      // Prepare receiving items with quantities and notes
      const items = inboundOrder?.items.map(item => ({
        detailId: item.id,
        quantityReceived: quantities[item.id] || 0,
        notes: notes[item.id] || ""
      })) || []

      console.log('Completing receiving with items:', items)

      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${inboundId}/complete-receiving`,
        { items },
        { withCredentials: true }
      )
      
      toast({ title: "Receiving completed successfully" })
      await fetchInboundOrder()
    } catch (error) {
      console.error('Error completing receiving:', error)
      toast({ title: "Failed to complete receiving", variant: "destructive" })
    }
  }

  const handleCancelInbound = async () => {
    try {
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/inbound-command/${inboundId}/cancel`,
        {},
        { withCredentials: true }
      )
      toast({ title: "Inbound order cancelled" })
      await fetchInboundOrder()
    } catch (error) {
      console.error('Error cancelling inbound:', error)
      toast({ title: "Failed to cancel inbound order", variant: "destructive" })
    }
  }

  // ================== ACTION BUTTON CONFIG ==================
  const getActionButtons = (currentStatus: InboundStatus) => {
    const nextStatuses = NextStatusMap[currentStatus] || []
    const buttons = []

    if (nextStatuses.includes('CONFIRMED')) {
      buttons.push({
        label: 'Confirm Order',
        onClick: handleConfirmInbound,
        icon: CheckCircle,
        variant: 'default' as const,
        className: 'bg-blue-600 hover:bg-blue-700'
      })
    }

    if (nextStatuses.includes('RECEIVING')) {
      buttons.push({
        label: 'Start Receiving',
        onClick: handleStartReceiving,
        icon: PackageOpen,
        variant: 'default' as const,
        className: 'bg-yellow-600 hover:bg-yellow-700'
      })
    }

    if (nextStatuses.includes('RECEIVED')) {
      buttons.push({
        label: 'Complete Receiving',
        onClick: handleCompleteReceiving,
        icon: PackageCheck,
        variant: 'default' as const,
        className: 'bg-green-600 hover:bg-green-700'
      })
    }

    if (nextStatuses.includes('CANCELLED')) {
      buttons.push({
        label: 'Cancel Order',
        onClick: handleCancelInbound,
        icon: XCircle,
        variant: 'destructive' as const,
        className: ''
      })
    }

    return buttons
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
  const actionButtons = getActionButtons(inboundOrder.status)

  return (
    <div className='bg-gray-100 min-h-screen w-full'>
      <Header 
        title="Inbound Order Details" 
        subtitle={`Manage receiving process for ${inboundOrder.inboundCode}`} 
      />
      
      <div className='flex flex-col p-4 gap-4'>
        {/* Basic Info Card */}
        <Card className='w-full'>
          <CardContent className='pt-6'>
            <div className='flex justify-between items-start mb-4'>
              <CardTitle className='text-lg font-semibold'>Order Information</CardTitle>
              
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
            
            <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4'>
              <div>
                <p className='text-sm text-gray-500'>Inbound Code</p>
                <p className='font-medium text-blue-600'>{inboundOrder.inboundCode}</p>
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
                <p className='text-sm text-gray-500'>Warehouse ID</p>
                <p className='font-mono text-xs text-gray-600'>{inboundOrder.warehouseId}</p>
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
              Products to Receive ({inboundOrder.items.length} items)
            </CardTitle>
            
            <div className='space-y-4'>
              {inboundOrder.items.map((item) => (
                <div 
                  key={item.id} 
                  className='border rounded-lg p-4 bg-white hover:shadow-md transition-shadow'
                >
                  <div className='flex flex-col gap-4'>
                    {/* Product Info */}
                    <div className='flex justify-between items-start'>
                      <div className='flex-1'>
                        <h3 className='font-semibold text-base text-gray-800'>
                          {item.productName}
                        </h3>
                        <p className='text-sm text-gray-500 mt-1'>
                          Variant ID: <span className='font-mono text-xs'>{item.productVariantId}</span>
                        </p>
                        <p className='text-sm font-medium text-green-600 mt-1'>
                          {formatCurrency(item.unitPrice)}
                        </p>
                      </div>

                      {/* Expected Quantity Badge */}
                      <div className='bg-blue-50 px-3 py-2 rounded-lg text-center'>
                        <p className='text-xs text-blue-600 font-medium'>Expected</p>
                        <p className='text-xl font-bold text-blue-700'>{item.quantityExpected}</p>
                      </div>
                    </div>
                    
                    {/* Quantity Controls */}
                    <div className='flex flex-col md:flex-row items-start md:items-center gap-4 pt-3 border-t'>
                      {/* Show current received quantity if exists */}
                      {item.quantityReceived > 0 && inboundOrder.status !== 'RECEIVED' && (
                        <div className='bg-green-50 px-3 py-2 rounded-lg text-center'>
                          <p className='text-xs text-green-600 font-medium'>Current</p>
                          <p className='text-lg font-bold text-green-700'>{item.quantityReceived}</p>
                        </div>
                      )}

                      {/* Only show controls if status is RECEIVING */}
                      {inboundOrder.status === 'RECEIVING' && (
                        <>
                          <div className='flex items-center gap-2'>
                            <p className='text-sm font-medium text-gray-600 mr-2'>Update:</p>
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
                              value={quantities[item.id] || 0}
                              onChange={(e) => handleQuantityChange(item.id, e.target.value)}
                              className="w-24 text-center font-semibold"
                            />
                            
                            <Button
                              variant="outline"
                              size="icon"
                              className="h-8 w-8"
                              onClick={() => handleIncrement(item.id, item.quantityExpected)}
                            >
                              <Plus className="h-4 w-4" />
                            </Button>

                            <Button
                              size="sm"
                              onClick={() => handleUpdateQuantity(item.id)}
                              className="ml-2"
                            >
                              Update
                            </Button>

                            {/* Difference indicator */}
                            {quantities[item.id] !== item.quantityExpected && (
                              <div className={`ml-2 px-2 py-1 rounded text-xs font-medium ${
                                quantities[item.id] > item.quantityExpected 
                                  ? 'bg-orange-100 text-orange-700'
                                  : 'bg-red-100 text-red-700'
                              }`}>
                                {quantities[item.id] > item.quantityExpected ? '+' : ''}
                                {quantities[item.id] - item.quantityExpected}
                              </div>
                            )}
                          </div>

                          {/* Notes Input */}
                          <div className='flex-1 min-w-[200px]'>
                            <Input
                              disabled
                              placeholder="Add notes (e.g., damage, quality issues, overage)"
                              value={notes[item.id] || ''}
                              onChange={(e) => handleNotesChange(item.id, e.target.value)}
                              className="w-full"
                            />
                          </div>
                        </>
                      )}

                      {/* Show received quantity and notes if already received */}
                      {inboundOrder.status === 'RECEIVED' && (
                        <div className='w-full'>
                          <div className='flex items-center gap-4'>
                            <div className='bg-green-50 px-4 py-2 rounded-lg text-center'>
                              <p className='text-xs text-green-600 font-medium'>Received</p>
                              <p className='text-xl font-bold text-green-700'>{item.quantityReceived}</p>
                            </div>

                            {item.quantityReceived !== item.quantityExpected && (
                              <div className='flex items-center gap-2'>
                                <div className={`px-3 py-2 rounded-lg ${
                                  item.quantityReceived > item.quantityExpected
                                    ? 'bg-orange-100 text-orange-700'
                                    : 'bg-red-100 text-red-700'
                                }`}>
                                  <p className='text-xs font-medium'>Difference</p>
                                  <p className='text-lg font-bold'>
                                    {item.quantityReceived > item.quantityExpected ? '+' : ''}
                                    {item.quantityReceived - item.quantityExpected}
                                  </p>
                                </div>
                              </div>
                            )}
                          </div>

                          {/* Show existing notes if any */}
                          {item.notes && (
                            <div className='bg-yellow-50 border border-yellow-200 rounded p-3 mt-3'>
                              <p className='text-xs text-yellow-800 font-medium mb-1'>📝 Notes:</p>
                              <p className='text-sm text-yellow-900'>{item.notes}</p>
                            </div>
                          )}
                        </div>
                      )}

                      {/* Show static info for other statuses */}
                      {!['RECEIVING', 'RECEIVED'].includes(inboundOrder.status) && (
                        <div className='text-sm text-gray-500 italic'>
                          Start receiving to update quantities
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Summary Section */}
            {inboundOrder.status === 'RECEIVED' && (
              <div className='mt-6 pt-6 border-t'>
                <h3 className='text-base font-semibold mb-4'>Receiving Summary</h3>
                <div className='grid grid-cols-2 md:grid-cols-4 gap-4'>
                  <div className='bg-gray-50 p-4 rounded-lg text-center'>
                    <p className='text-sm text-gray-600 mb-1'>Total Items</p>
                    <p className='text-2xl font-bold text-gray-700'>
                      {inboundOrder.items.length}
                    </p>
                  </div>
                  <div className='bg-blue-50 p-4 rounded-lg text-center'>
                    <p className='text-sm text-blue-600 mb-1'>Expected</p>
                    <p className='text-2xl font-bold text-blue-700'>
                      {inboundOrder.items.reduce((sum, item) => sum + item.quantityExpected, 0)}
                    </p>
                  </div>
                  <div className='bg-green-50 p-4 rounded-lg text-center'>
                    <p className='text-sm text-green-600 mb-1'>Received</p>
                    <p className='text-2xl font-bold text-green-700'>
                      {inboundOrder.items.reduce((sum, item) => sum + item.quantityReceived, 0)}
                    </p>
                  </div>
                  <div className='bg-orange-50 p-4 rounded-lg text-center'>
                    <p className='text-sm text-orange-600 mb-1'>Difference</p>
                    <p className={`text-2xl font-bold ${
                      inboundOrder.items.reduce((sum, item) => 
                        sum + (item.quantityReceived - item.quantityExpected), 0
                      ) === 0 
                        ? 'text-gray-700'
                        : 'text-orange-700'
                    }`}>
                      {inboundOrder.items.reduce((sum, item) => 
                        sum + (item.quantityReceived - item.quantityExpected), 0
                      ) > 0 ? '+' : ''}
                      {inboundOrder.items.reduce((sum, item) => 
                        sum + (item.quantityReceived - item.quantityExpected), 0
                      )}
                    </p>
                  </div>
                </div>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Back Button */}
        <div className='flex justify-start'>
          <Button
            variant="outline"
            onClick={() => router.push('/admin/orders')}
          >
            ← Back to Orders
          </Button>
        </div>
      </div>
    </div>
  )
}

export default InboundOrderDetails