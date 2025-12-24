import React from 'react'

const StatusDisplay = ({ status }: { status: string }) => {
  return (
    <div>
        {status === "DRAFT" && (
            <span className="px-2 py-1 rounded-full bg-gray-100 text-gray-600 text-xs font-semibold">
                {status}
            </span>
        )}
        {status === "ACTIVE" && (
            <span className="px-2 py-1 rounded-full bg-green-100 text-green-600 text-xs font-semibold">
                {status}
            </span>
        )}
        {status === "INACTIVE" && (
            <span className="px-2 py-1 rounded-full bg-yellow-100 text-yellow-600 text-xs font-semibold">
                {status}
            </span>
        )}
        {status === "BANNED" && (
            <span className="px-2 py-1 rounded-full bg-red-100 text-red-600 text-xs font-semibold">
                {status}
            </span>
        )}
        {status === "SUSPENDED" && (
            <span className="px-2 py-1 rounded-full bg-rose-100 text-rose-600 text-xs font-semibold">
                {status}
            </span>
        )}
    </div>
  )
}

export default StatusDisplay