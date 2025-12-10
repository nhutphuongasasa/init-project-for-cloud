# Kết nối vào bất kỳ node nào trong cluster (ví dụ 7001)
redis-cli -c -p 7001

# 1. Kiểm tra xem có key session nào không
KEYS "gateway:sessions:*" | wc -l
# hoặc nếu bạn không đổi namespace thì:
KEYS "spring:session:sessions:*" | wc -l

# → Nếu trả về số > 0 → session đang lưu trong cluster

# 2. Xem nội dung 1 session thật (lấy key đầu tiên)
KEYS "gateway:sessions:*" | head -1 | xargs redis-cli -c -p 7001 HGETALL
# → Sẽ thấy các field như:
#   creationTime, maxInactiveInterval, lastAccessedTime, sessionAttr:SPRING_SECURITY_CONTEXT, ...

# 3. Xem TTL (thời gian sống còn lại)
KEYS "gateway:sessions:*" | head -1 | xargs redis-cli -c -p 7001 TTL
# → Thường là 1800 giây (30 phút) hoặc số dương → đúng