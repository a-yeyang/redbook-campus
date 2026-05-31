local key = KEYS[1]
local noteId = ARGV[1]

local exists = redis.call('EXISTS', key)
if exists == 0 then
    return -1
end

local isCollected = redis.call('SISMEMBER', key, noteId)
if isCollected == 1 then
    return 1
end

redis.call('SADD', key, noteId)
return 0
