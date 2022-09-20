import java.util.ArrayList;
import java.util.Comparator;

public class SJF {
    public int clock;
    public int idle;

    public ArrayList<Process> ReadyQueue = new ArrayList<Process>();

    public SJF(){
        clock = 0;
        idle = 0;
    }

    public void run(ArrayList<Process> poll , ArrayList<String> result, Criteria criteria, ArrayList<Process> procedure){

        poll.sort(Comparator.comparing(Process::getArriveTime));

        schedule(poll, result, procedure);

        System.out.println("==================== SJF ====================");

        double avgCPUUilization = getAverageCPUU();
        criteria.avgCPUU = getAverageCPUU();
        double avgWT = getWaitingTime(poll);
        criteria.avgWT = getWaitingTime(poll);
        double avgRT = getResponseTime(poll);
        criteria.avgRT = getResponseTime(poll);
        double avgTT = getAverageTurnaroundTime(poll);
        criteria.avgTT = getAverageTurnaroundTime(poll);

        System.out.println("==================== SJF FIN ====================");
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

        while(true){
            while((job != pNum) && ReadyQueue.isEmpty() ){ // 레디큐가 비어있고 작업 poll에서 할당받지 못한 프로세스가 남아있다면
                if(poll.get(job).arriveTime == clock){ // 프로세스의 도착시간이 현재 clock이라면
                    ReadyQueue.add(poll.get(job));
                    p = ReadyQueue.get(0);
                    p.responseTime = clock - p.arriveTime;
                    job++;
                    break;
                }
                else{
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

            ReadyQueue.sort(Comparator.comparing(Process::getCpuBurst));

            if(!ReadyQueue.isEmpty()){ // 레디큐에 프로세스가 있을 때
                p = ReadyQueue.remove(0);
                p.swapIn = clock;

                if(p.burstLeft == p.cpuBurst){
                    p.responseTime = clock - p.arriveTime;
                }

                if(p.burstLeft != 0){
                    while(p.burstLeft > 0){
                        System.out.println(clock + " : 프로세스 " + p.PID + " is Running");
                        result.add(clock + " : 프로세스 " + p.PID + " is Running");
                        clock++;
                        p.burstLeft = p.burstLeft- 1;

                        if(job != pNum && poll.get(job).arriveTime == clock){
                            ReadyQueue.add(poll.get(job));
                            job++;
                            ReadyQueue.sort(Comparator.comparing(Process::getCpuBurst));
                        }
                    }
                }

                if(p.burstLeft == 0){
                    p.swapOut = clock;
                    p.completedTime = clock;
                    p.turnaroundTime = p.completedTime - p.arriveTime;
                    p.waitTime = p.completedTime - p.arriveTime - p.cpuBurst;
                    System.out.println(clock + " : 프로세스 " + p.PID + " is Exit");
                    result.add(clock + " : 프로세스 " + p.PID + " is Exit\n");
                    procedure.add(p);

                }
            }
            if(ReadyQueue.isEmpty() && job == pNum){
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
//            System.out.println(p.waitTime);
            sum += p.waitTime;
        }

        double avg =  ((double)sum / poll.size());

        return avg;
    }

    private double getResponseTime(ArrayList<Process>poll){
        int sum = 0;
        for(Process p : poll){
//            System.out.println(p.responseTime);
            sum += p.responseTime;
        }
        double avg =  ((double)sum / poll.size());

        return avg;
    }
}
