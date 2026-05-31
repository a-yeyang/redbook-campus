local key = KEYS[1]
local noteId = ARGV[1]

local exists = redis.call('EXISTS', key)
if exists == 0 then
    return -1
end

local isLiked = redis.call('SISMEMBER', key, noteId)
if isLiked == 0 then
    return 0
end

return redis.call('SREM', key, noteId)
