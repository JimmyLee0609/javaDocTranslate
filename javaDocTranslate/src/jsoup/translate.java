package jsoup;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class translate {

	public static void main(String[] args) throws IOException {
		String path="D:\\jdk源码分析\\注解翻译\\";
		String readName="HashMap.txt";
		String writeName="HashMap转.txt";
		arrangeJavaDoc(path+readName, path+writeName);
		
	}

	private static void arrangeJavaDoc(String readPath, String writePath) throws FileNotFoundException, IOException {
		FileReader fileReader = new FileReader(readPath);
		FileWriter writer = new FileWriter(writePath);
		char[] array = new char[1024];
		int readLength = 0;
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		while ((readLength = fileReader.read(array, 0, 1024)) > 0) {

			char[] arrangeStart = arrangeStart(array, readLength,tempList);
			char[] senctenceInLine = fixInLine(arrangeStart,tempList);
			char[] arrangeLine = arrangeLine(senctenceInLine,tempList);
			writer.write(arrangeLine);
		}
		writer.close();
		fileReader.close();
	}

	private static char[] fixInLine(char[] array,ArrayList<Integer> falseLineMark) {
		// �Ҷ���Ļ��з�������λ��
		for (int i = 0; i < array.length; i++) {
			if (array[i] == '\r' &&i!=0&& array[i - 1] != '.') {
				falseLineMark.add(i);
			}
		}
		char[] temp = new char[array.length - falseLineMark.size() * 2];
		int pos = 0, last = 0;
		for (Integer integer : falseLineMark) {
			System.arraycopy(array, last, temp, pos, integer - last);
			pos += integer - last;
			last = integer + 2;
		}

		System.arraycopy(array, last, temp, pos, array.length - last);
		falseLineMark.clear();
		return temp;
	}

	private static char[] arrangeStart(char[] array, int readLength,ArrayList<Integer> startMark) {

		// ѭ�������������Ƿ���� * ����¼λ��
		for (int i = 0; i < readLength; i++) {
			if (array[i] == '*') {
				startMark.add(i + 1);
			}
		}
		// *��ǰ����5���ո�ÿ����������ͬ5���ո�һ��ɾ��
		char[] arr = new char[readLength - startMark.size() * 5];
		// ��¼��һ��Դ����ĽǱ꣬��¼��������Ŀǰ��ĩβ�Ǳ�
		int temp = 0, pos = 0;

		for (Integer integer : startMark) {
			// ��Ҫ�ȼ�¼һ�� * ���ֵĽǱ�
			if (temp < 1) {
				temp = integer;
				continue;
			} else {

				System.arraycopy(array, temp, arr, pos, integer - temp - 6);
				// ��¼���Ƶĳ��ȡ�������һ�θ���ʱ�����������ʼλ��
				pos += integer - temp - 6;
				// ��¼��һ��������λ�ã�������һ�ε���λ��
				temp = integer;
			}
		}
		startMark.clear();
		System.arraycopy(array, temp, arr, pos, readLength - temp);
		return arr;
	}

	private static char[] arrangeLine(char[] array,ArrayList<Integer> nextLineMark) {
		// ����Ӧ�û��еĵط�������Īλ��
		for (int i = 0; i < array.length; i++) {
			if (array[i] == '.' && i + 1 < array.length && (array[i + 1] == ' ' || array[i + 1] == ')'))
				nextLineMark.add(i + 2);
		}
		// �½�������װ�ؼ��ϻ��з�������
		char[] tempChar = new char[array.length + nextLineMark.size() * 2];
		// ��һ���ҵ�������λ�� ������װ�ص�Īλ��
		int temp = 0, pos = 0;
		// �����ҵ��Ļ���λ��
		for (Integer integer : nextLineMark) {
			System.arraycopy(array, temp, tempChar, pos, integer - temp);
			// ÿ��������λ�ø���Ϊ ԭ��λ�ü����¼ӵĳ���
			pos += integer - temp;
			tempChar[pos] = '\r';
			tempChar[pos + 1] = '\n';
			pos += 2;
			// ��������λ�ã�������һ�θ��Ƶ���λ��
			temp = integer;

		}
		// u���ĩβ������д��
		System.arraycopy(array, temp, tempChar, pos, array.length - temp);
		int post=tempChar.length;
		for(int i=tempChar.length-1;i>0;i--){
			if(tempChar[i]==0){
				post-=1;
			}
		}
		char [] resultString=new char[post];
		System.arraycopy(tempChar, 0, resultString, 0, post);
		nextLineMark.clear();
		return resultString;
	}

}
