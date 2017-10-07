package jsoup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupTest {

	public static void main(String[] args) throws IOException {
		String path="D:\\jdk源码分析\\java\\util\\concurrent";
		String name="ConcurrentSkipListMap.html";
		File in = new File(path+"\\"+name);
		
		Document parse = Jsoup.parse(in, "gbk");
		FileWriter fileWriter = new FileWriter("D:\\jdk源码分析\\jsoupForTest\\"+name+".txt");
		
		Elements select = parse.select("div.block");
		TestTraversor traversor = new TestTraversor();
		// 遍历所有 div.block的元素
		for (Element element : select) {
			traversor.traverse(element);
		}
		Map<Element,String> getaBlockText = traversor.getaBlockText();
		for(Entry<Element,String> en: getaBlockText.entrySet()){
			Element key = en.getKey();
			String value = en.getValue();
			WriteTodisck(fileWriter,value);
		}
		fileWriter.close();
		/* readDodeText(select);*/
	}

	@SuppressWarnings("unused")
	private static void readDodeText(Elements select) {
		for (Element element : select) {
			Elements allElements = element.getAllElements();
			// 获取元素内的子元素
			for (Element element2 : allElements) {
				String text = element2.html();
			}
		}
	}

	private static void WriteTodisck(FileWriter fileWriter,String text) throws IOException {
		fileWriter.write(text+System.lineSeparator());
	}


	static int size;

	private static void findAllNode(Elements select, ArrayList<Element> list) {
		for (int i = 0, hasElements = select.size(); i < hasElements; i++) {
			Element element = select.get(i);
			Elements allElements = element.getAllElements();
			if (allElements.size() > 1) {
				findchildNode(allElements, list);
			} else
				list.add(element);
		}
	}

	private static void findchildNode(Elements allElements, ArrayList<Element> list) {
		for (int i = 1, hasElements = allElements.size(); i < hasElements; i++) {
			Element element = allElements.get(i);
			Elements childs = element.getAllElements();
			if (childs.size() > 1) {
				findchildNode(childs, list);
			} else
				list.add(element);
		}
	}

}
