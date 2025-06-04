local key = KEYS[1]
local requested = tonumber(ARGV[1])
local current = tonumber(redis.call('GET', key))

if current == nil or current == 0 then
  return 0  -- 처리된 수량 0
elseif current >= requested then
  redis.call('DECRBY', key, requested)
  return requested  -- 요청한 수량 모두 처리
else
  redis.call('DECRBY', key, current)
  return current  -- 일부만 처리(남은 수량 만큼)
end
