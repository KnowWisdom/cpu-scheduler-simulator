import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FCFS {
    public int clock;
    public int idle;

    public ArrayList<Process> ReadyQueue = new ArrayList<Process>();

    public FCFS(){
        clock = 0;
        idle = 0;
    }

    public void run(ArrayList<Process> poll , ArrayList<String> result, Criteria criteria, ArrayList<Process> procedure){

        poll.sort(Comparator.comparing(Process::getArriveTime));

        schedule(poll, result, procedure);

        System.out.println("==================== FCFS ====================");

        double avgCPUUilization = getAverageCPUU();
        criteria.avgCPUU = getAverageCPUU();
        double avgWT = getWaitingTime(poll);
        criteria.avgWT = getWaitingTime(poll);
        double avgRT = getResponseTime(poll);
        criteria.avgRT = getResponseTime(poll);
        double avgTT = getAverageTurnaroundTime(poll);
        criteria.avgTT = getAverageTurnaroundTime(poll);

        System.out.println("==================== FCFS FIN ====================");
        System.out.println("CPU 이용률 : " + avgCPUUilization );
        System.out.println("평균 총처리 시간 : " + avgTT );
        System.out.println("평균 대기시간 : " + avgWT );
        System.out.println("평균 응답시간 : " + avgRT );
        System.out.println("==================================================");

        for(int i=0;i<procedure.size();i++){
            System.out.println(procedure.get(i).PID + " : " + procedure.get(i).swapIn + " " + procedure.get(i).swapOut);
        }

    }

    public void schedule(ArrayList<Process> poll, ArrayList<String> result, ArrayList<Process> procedure){
        int pNum = poll.size();
        int job = 0; // 현재 job
        int cpuBurst = 0; // 현재 프로세스가 진행한 burst time

        Process p;

        while(poll.get(job).arriveTime != clock){ // 첫 프로세스가 0보다 늦게 들어올 경우
            System.out.println(clock + " : " + "IDLE"); // 첫 프로세스가 들어올때까지 idle
            idle++;
            clock++;
        }

        ReadyQueue.add(poll.get(job)); // Poll에서 첫번째 작업을 가져와 Ready Queue에 넣기
        job++;
        p = ReadyQueue.get(0); // p는 현재 CPU를 할당 받고 있는 프로세스를 의미함
        p.responseTime = clock - p.arriveTime; // 응답시간 = 현재 클락 - 도착시간


        while(true){
            while((job != pNum) && ReadyQueue.isEmpty()){ // Ready Queue가 비어있고 Poll에 프로세스가 남아있다면
                if(poll.get(job).arriveTime == clock){ // 현재 clock과 도착시간이 일치한 프로세스가 있다면
                    ReadyQueue.add(poll.get(job)); // Ready Queue에 넣기
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

            // 도착시간이 같은 다른 프로세스가 있는지 확인
            while((job != pNum) && (poll.get(job).arriveTime == clock)){
                ReadyQueue.add(poll.get(job));
                job++;
            }

            if(cpuBurst == 0) {
                p.swapIn = clock;
            }

            // 현재 버스트 시간과 프로세스의 버스트 시간이 같으면 프로세스는 할일을 끝마친 것
            if(cpuBurst == p.cpuBurst){
                p.swapOut = clock;
                p.completedTime = clock;
                p.turnaroundTime = p.completedTime - p.arriveTime;
                p.waitTime = p.completedTime - p.arriveTime - p.cpuBurst;

                System.out.println(clock + " : 프로세스 " + p.PID + " is Exit");
                result.add(clock + " : 프로세스 " + p.PID + " is Exit");
                procedure.add(p);

                ReadyQueue.remove(0); // 처리완료한 프로세스 exit

                cpuBurst = 0;

                if(ReadyQueue.isEmpty() && (job == pNum)){ // 마지막 프로세스까지 처리 완료
                    break;
                }
                else if(ReadyQueue.isEmpty() == false){ // 새로운 CPU 할당
                    p = ReadyQueue.get(0);
                    p.responseTime = clock - p.arriveTime;
                }
            }
            else if(cpuBurst != p.cpuBurst){
                System.out.println(clock + " : 프로세스 " + p.PID + " is Running");
                result.add(clock + " : 프로세스 " + p.PID + " is Running");
                cpuBurst++;
                clock++;
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
            sum += p.responseTime;
        }
        double avg =  ((double)sum / poll.size());

        return avg;
    }

}