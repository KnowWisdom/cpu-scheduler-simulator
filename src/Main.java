import java.io.*;
import java.util.*;

public class Main {

    static ArrayList<String> result = new ArrayList<String>();
    static Criteria criteria = new Criteria();
    static ArrayList<Process> procedure = new ArrayList<Process>();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        FileReader fd = null;

        String path = Main.class.getResource("").getPath();

        System.out.println(path);

        ArrayList<Process> jobs = new ArrayList<Process>();
        Process process;

        System.out.print("FCFS, SJF, SRTF, RR 중 스케줄링 정책 선택하세요 >> ");
        String type = sc.nextLine();

        try{
            fd = new FileReader(path + "processes.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(fd);
        String str = null;

        try{
            int pid, arrive, burst;

            while((str=br.readLine()) != null){

                int len = str.length();

                if(len > 0){
                    StringTokenizer st = new StringTokenizer(str, " ");
                    pid = Integer.parseInt(st.nextToken());
                    arrive = Integer.parseInt(st.nextToken());
                    burst = Integer.parseInt(st.nextToken());


                  process = new Process(pid, arrive, burst);
                  jobs.add(process);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        switch(type){
            case "FCFS" :
                FCFS f = new FCFS();
                f.run(jobs, result, criteria, procedure);

                break;
            case "SJF" :
                SJF sj = new SJF();
                sj.run(jobs, result, criteria, procedure);
                break;
            case "SRTF" :
                SRTF sr = new SRTF();
                sr.run(jobs, result, criteria, procedure);
                break;
            case "RR" :
                System.out.print("RR 정책의 타임 슬라이스를 입력해주세요. >> ");
                int timeslice = sc.nextInt();
                RR rr = new RR(timeslice);
                rr.run(jobs, result, criteria, procedure);
                break;
            default:
                System.out.println("선택지에 없는 정책이거나 올바른 입력이 아닙니다.");
                break;
        }

        Simulator sim = new Simulator(type, procedure, result, criteria , jobs);

    }
}
