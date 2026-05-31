local key = KEYS[1]

for i = 1, #ARGV - 1 do
    redis.call('SADD', key, ARGV[i])
end

local expireSeconds = ARGV[#ARGV]
redis.call('EXPIRE', key, expireSeconds)
return 0
