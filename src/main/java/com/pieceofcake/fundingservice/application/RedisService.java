package com.pieceofcake.fundingservice.application;

public interface RedisService {
    long join(String fundingUuid, int quantity);
    boolean leave();
}
