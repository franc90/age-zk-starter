package org.age.zk.services.worker;

public enum GlobalComputationState {

    INIT,
    COMPUTING,
    FINISHED;

    public byte[] toBytes() {
        return toString().getBytes();
    }

    public static GlobalComputationState fromBytes(byte[] data) {
        String value = new String(data);
        return GlobalComputationState.valueOf(value);
    }

}
