import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class RR {
    public int clock;
    public int idle;
    public int timeslice;

    public ArrayList<Process> ReadyQueue = new ArrayList<Process>();
    public Process clone;

    public RR(int timeslice){
        clock = 0;
        idle = 0;
        this.timeslice = timeslice;
    }

    public void run(ArrayList<Process> poll , ArrayList<String> result, Criteria criteria, ArrayList<Process> procedure){

        poll.sort(Comparator.comparing(Process::getArriveTime));

        schedule(poll, result, procedure);

        System.out.println("==================== RR ====================");

        double avgCPUUilization = getAverageCPUU();
        criteria.avgCPUU = getAverageCPUU();
        double avgWT = getWaitingTime(poll);
        criteria.avgWT = getWaitingTime(poll);
        double avgRT = getResponseTime(poll);
        criteria.avgRT = getResponseTime(poll);
        double avgTT = getAverageTurnaroundTime(poll);
        criteria.avgTT = getAverageTurnaroundTime(poll);

        System.out.println("==================== RR FIN ====================");
        System.out.println("CPU 이용률 : " + avgCPUUilization );
        System.out.println("평균 총처리 시간 : " + avgTT );
        System.out.println("평균 대기시간 : " + avgWT );
        System.out.println("평균 응답시간 : " + avgRT );
        System.out.println("==================================================");

    }

    public void schedule(ArrayList<Process> poll, ArrayList<String> result, ArrayList<Process> procedure){
        int pNum = poll.size();
        int job = 0; // 현재 job


        Process p = null;

        while(poll.get(job).arriveTime != clock){ // 첫 프로세스가 0보다 늦게 들어올 경우
            System.out.println(clock + " : " + "IDLE");
            idle++;
            clock++;
        }

        // 현재 프로세스를 레디큐에 할당
        ReadyQueue.add(poll.get(job));
        job++;
        p = ReadyQueue.get(0);

        while(true) {
            while ((job != pNum) && ReadyQueue.isEmpty()) { // 레디큐가 비어있고 작업 poll에서 할당받지 못한 프로세스가 남아있다면
                if (poll.get(job).arriveTime == clock) { // 프로세스의 도착시간이 현재 clock이라면
                    ReadyQueue.add(poll.get(job));
                    job++;
                } else {
                    System.out.println(clock + " : IDLE");
                    idle++;
                    clock++;
                }
            }

            // 도착시간이 같은 프로세스가 있는지 확인
            while((job != pNum) && (poll.get(job).arriveTime == clock)){
                ReadyQueue.add(poll.get(job));
                job++;
            }

            for(int i=0 ; i< timeslice; i++){
                if(p.burstLeft != 0){
                    System.out.println(clock + " : 프로세스 " + p.PID + " is Running");
                    result.add(clock + " : 프로세스 " + p.PID + " is Running");
                    clock++;
                    p.burstLeft = p.burstLeft-1;
                    while( (job != pNum) && (poll.get(job).arriveTime == clock)){
                        ReadyQueue.add(poll.get(job));
                        job++;
                    }
                    if(p.burstLeft == 0){
                        p.completedTime = clock;
                        p.turnaroundTime = p.completedTime - p.arriveTime;
                        p.waitTime = p.completedTime - p.arriveTime - p.cpuBurst;
                        System.out.println(clock + " : 프로세스 " + p.PID + " is Exit");
                        result.add(clock + " : 프로세스 " + p.PID + " is Exit");

                        break;
                    }
                }
            }
            if(ReadyQueue.isEmpty() == false) {
                if (p.burstLeft == 0) {
                    ReadyQueue.remove(0);
                    clone = (Process) p.clone();
                    clone.swapOut = clock;
                    procedure.add(clone);
                    if(ReadyQueue.isEmpty() == false){
                        p = ReadyQueue.get(0);
                        p.swapIn = clock;
                        if(p.burstLeft == p.cpuBurst){
                            p.responseTime = clock - p.arriveTime;
                        }
                    }
                }
                else{
                    Collections.rotate(ReadyQueue, ReadyQueue.size()-1);
//                    System.out.println(ReadyQueue.get(0));
                    clone = (Process) p.clone();
                    clone.swapOut = clock;
                    procedure.add(clone);
                    p = ReadyQueue.get(0);
                    p.swapIn = clock;
                    if(p.burstLeft == p.cpuBurst){
                        p.responseTime = clock - p.arriveTime;
                    }
                }
            }
            else if(ReadyQueue.isEmpty() && job == pNum){
                break;
            }
        }
    }

    private double getAverageCPUU(){
        double avg = ((double)(clock - idle) / clock) ;
        avg *= 100; // 백분율
        return avg;
    }

    private double getAverageTurnaroundTime(ArrayList<Process>poll){
        int sum = 0;
        for(Process p : poll){
            sum += p.turnaroundTime;
        }
        double avg =  ((double)sum / poll.size());

        return avg;
    }

    private double getWaitingTime(ArrayList<Process>poll){
        int sum = 0;
        for(Process p : poll){
            sum += p.waitTime;
        }

        double avg =  (double)sum/poll.size();

        return avg;
    }

    private double getResponseTime(ArrayList<Process>poll){
        int sum = 0;
        for(Process p : poll){
            sum += p.responseTime;
        }
        double avg =  (double)sum/poll.size();


        return avg;
    }

}
