local remain = tonumber(redis.call('GET', KEYS[1]) or '0')
local increment = tonumber(ARGV[1])
local total = tonumber(redis.call('GET', KEYS[2]) or '0')

local newRemain = remain + increment
if newRemain > total then
    return 0
else
    redis.call('INCRBY', KEYS[1], increment)
    return 1
end