package jsoup;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

//获取text的方法实现
public class TestTraversor {
	NodeVisitor visitor;

	public TestTraversor() {
		visitor = new MyNodeVisitor();
	}

	public void traverse(Node root) {
		Node node = root;
		int depth = 0;

		while (node != null) {
			visitor.head(node, depth);
			if (node.childNodeSize() > 0) { // 如果 有子节点 层+1 获取第一个子节点 <div > <tr>
											// <td></td> </tr> </div>
				node = node.childNode(0);
				depth++;
			} else {
				while (node.nextSibling() == null && depth > 0) { // 如果下一个节点是null，没有下一个节点了
																	// 层>0
					visitor.tail(node, depth);
					node = node.parentNode(); // 节点 回到 父节点
					depth--; // 层-1
				} // end while 这一层遍历完了， 往会找父节点
				visitor.tail(node, depth);
				if (node == root) // 如果回到根节点 退出循环 就是遍历完了
					break;
				node = node.nextSibling(); // 下一个节点
			}
		}
	}

	LinkedHashMap<Element,String> aBlockText = new LinkedHashMap<Element,String>();

	public HashMap<Element,String> getaBlockText() {
		return aBlockText;
	}

	StringBuilder temp = new StringBuilder();

	class MyNodeVisitor implements NodeVisitor {
		final StringBuilder accum = new StringBuilder();

		@Override
		public void head(Node node, int depth) {
			if (node instanceof TextNode) {
				TextNode textNode = (TextNode) node;
				appendNormalisedText(accum, textNode);
			} else if (node instanceof Element) {
				Element element = (Element) node;
				if (accum.length() > 0 && (element.isBlock() || element.tagName().equals("br"))
						&& !lastCharIsWhitespace(accum))// return sb.length() !=
														// 0 &&
														// sb.charAt(sb.length()
														// - 1) == ' ';
					accum.append(" ");
				if (element.isBlock()&&temp.length()>0) {
					
					aBlockText.put(element,temp.toString());
					
					int length = temp.length();
					temp.delete(0, length);
				}

			}

		}

		@Override
		public void tail(Node node, int depth) {

		}

		private boolean lastCharIsWhitespace(StringBuilder sb) {
			return sb.length() != 0 && sb.charAt(sb.length() - 1) == ' ';
		}

		private void appendNormalisedText(StringBuilder accum2, TextNode textNode) {
			String text = textNode.getWholeText();

			if (preserveWhitespace(textNode.parentNode())) {
				accum.append(text);
				temp.append(text);
			} else
				appendNormalisedWhitespace(accum, text, lastCharIsWhitespace(accum));
		}

		public void appendNormalisedWhitespace(StringBuilder accum, String string, boolean stripLeading) {
			boolean lastWasWhite = false;
			boolean reachedNonWhite = false;

			int len = string.length();
			int c;
			for (int i = 0; i < len; i += Character.charCount(c)) {
				c = string.codePointAt(i);
				if (isActuallyWhitespace(c)) {
					if ((stripLeading && !reachedNonWhite) || lastWasWhite)
						continue;
					accum.append(' ');
					temp.append(' ');
					lastWasWhite = true;
				} else {
					accum.appendCodePoint(c);
					temp.appendCodePoint(c);
					lastWasWhite = false;
					reachedNonWhite = true;
				}
			}
		}

		private boolean preserveWhitespace(Node node) {
			if (node != null && node instanceof Element) {
				Element element = (Element) node;
				return element.tag().preserveWhitespace()
						|| element.parent() != null && element.parent().tag().preserveWhitespace();
			}
			return false;
		}

		public boolean isActuallyWhitespace(int c) {
			return c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r' || c == 160;
			// 160 is &nbsp; (non-breaking space). Not in the spec but expected.
		}
	}
}
