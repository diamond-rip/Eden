package rip.diamond.practice.events;

public enum EventState {

    /**
     * 該活動正在等待玩家的加入
     */
    WAITING,

    /**
     * 該活動正在準備開始
     * 這個情況下玩家依然可以進入活動
     */
    STARTING,

    /**
     * 該活動正在運行中
     */
    RUNNING,

    /**
     * 該活動正在結束
     */
    ENDING

}
