package util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ShellUtil {
	public static void callBack(String path,String cmd){
		Runtime runtime=Runtime.getRuntime();
		try {
			Process process=runtime.exec(cmd);
			int result=process.waitFor();
			if(result==0){
				System.out.println("执行成功");
			}else{
				System.out.println("执行失败 错误代码"+result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void callBack(String mainCmd,String []cmds){
		Runtime runtime=Runtime.getRuntime();
		Process process;
		try {
			process = runtime.exec(mainCmd);
			OutputStream outputStream=process.getOutputStream();
			PrintWriter printWriter=new PrintWriter(outputStream);
			for(String param : cmds){
				printWriter.write(param);
			}
			printWriter.flush();
			printWriter.close();
			int result=process.waitFor();
			if(result==0){
				System.out.println("执行成功");
			}else{
				System.out.println("执行失败 错误代码"+result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
