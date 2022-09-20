public class Process implements Comparable<Process>, Cloneable{
    public int PID;
    public int arriveTime;
    public int completedTime;
    public int cpuBurst;

    public int swapIn;
    public int swapOut;

    public int waitTime;
    public int turnaroundTime;
    public int responseTime;
    public int burstLeft;

    public Process(){};

    public Process(int pid, int arriveTime, int cpuBurst){
        this.PID = pid;
        this.arriveTime = arriveTime;
        this.cpuBurst = cpuBurst;
        this.burstLeft = cpuBurst;
    }

    public int getArriveTime() {
        return arriveTime;
    }

    public int getCpuBurst() {
        return cpuBurst;
    }

    public int getBurstLeft() {
        return burstLeft;
    }

    @Override
    public int compareTo(Process o) {
        return this.cpuBurst - o.cpuBurst;
    }

    @Override
    public Object clone(){
        Object o = null;
        try {
            o = (Object) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return (Process)o;
    }
}
