package com.batuhan.testlab.data.model.matrix

import com.google.gson.annotations.SerializedName

data class Shard(
    @SerializedName("shardIndex") val shardIndex: Int?,
    @SerializedName("numShards") val numShards: Int?,
    @SerializedName("testTargetsForShard") val testTargetsForShard: TestTargetsForShard?,
    @SerializedName("estimatedShardDuration") val estimatedShardDuration: String?
)

data class TestTargetsForShard(
    @SerializedName("targets") val targets: List<String>?
)
