package com.pieceofcake.fundingservice.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisServiceImpl implements RedisService{

    private final StringRedisTemplate redisTemplate;

    @Override
    public long join(String fundingUuid, int quantity) {
        try {
            // Lua 스크립트 파일 로드
            String script = new String(Files.readAllBytes(Paths.get("src/main/resources/scripts/join.lua")));

            // Redis 키와 인자 설정
            List<String> keys = Collections.singletonList(fundingUuid);
            List<String> args = Collections.singletonList(String.valueOf(quantity));

            // Lua 스크립트 실행 (처리된 수량, 남은 수량) 반환
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);

            return redisTemplate.execute(redisScript, keys, args.toArray(new String[0]));
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean leave() {
        return redisTemplate.delete("testKey");
    }

}
