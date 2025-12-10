export default function Loading() {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="flex flex-col items-center">
        {/* Vòng xoay loading */}
        <div className="h-12 w-12 animate-spin rounded-full border-4 border-white border-t-transparent"></div>
        <p className="mt-4 text-white">Đang xử lý...</p>
      </div>
    </div>
  )
}
