package tryhttp;

import java.net.Socket;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;

public class Get {
	public static void main(String[] args) throws Exception{
	 HttpProcessor httpproc = HttpProcessorBuilder.create()
	            .add(new RequestContent())
	            .add(new RequestTargetHost())
	            .add(new RequestConnControl())
	            .add(new RequestUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:55.0) Gecko/20100101 Firefox/55.0"))
	            .add(new RequestExpectContinue(true)).build();

	        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

	        HttpCoreContext coreContext = HttpCoreContext.create();
	        
	        HttpHost host = new HttpHost("translate.google.cn", 80);
	        
	        coreContext.setTargetHost(host);

	        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
	        ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;

	        try {

	            String[] targets = {
	                    "https://translate.google.cn",
	                    "/translate_a/single?client=t&sl=en&tl=zh-CN&hl=zh-CN&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&ie=UTF-8&oe=UTF-8&pc=1&otf=1&ssel=0&tsel=0&kc=1&tk=842763.704317&q=Resizable-array%20implementation%20of%20the%20List%20interface.%20Implements%20all%20optional%20list%20operations%2C%20and%20permits%20all%20elements%2C%20including%20null.%20In%20addition%20to%20implementing%20the%20List%20interface%2C%20this%20class%20provides%20methods%20to%20manipulate%20the%20size%20of%20the%20array%20that%20is%20used%20internally%20to%20store%20the%20list.%20(This%20class%20is%20roughly%20equivalent%20to%20Vector%2C%20except%20that%20it%20is%20unsynchronized.)%20%0AThe%20size%2C%20isEmpty%2C%20get%2C%20set%2C%20iterator%2C%20and%20listIterator%20operations%20run%20in%20constant%20time.%20The%20add%20operation%20runs%20in%20amortized%20constant%20time%2C%20that%20is%2C%20adding%20n%20elements%20requires%20O(n)%20time.%20All%20of%20the%20other%20operations%20run%20in%20linear%20time%20(roughly%20speaking).%20The%20constant%20factor%20is%20low%20compared%20to%20that%20for%20the%20LinkedList%20implementation."};

	            for (int i = 0; i < targets.length; i++) {
	                if (!conn.isOpen()) {
	                    Socket socket = new Socket(host.getHostName(), host.getPort());
	                    conn.bind(socket);
	                }
	                BasicHttpRequest request = new BasicHttpRequest("GET", targets[i]);
	                System.out.println(">> Request URI: " + request.getRequestLine().getUri());

	                httpexecutor.preProcess(request, httpproc, coreContext);
	                
	                HttpResponse response = httpexecutor.execute(request, conn, coreContext);
	                
//	                httpexecutor.postProcess(response, httpproc, coreContext);

	                System.out.println("<< Response: " + response.getStatusLine());
	                System.out.println(EntityUtils.toString(response.getEntity()));
	                System.out.println("==============");
	                if (!connStrategy.keepAlive(response, coreContext)) {
	                    conn.close();
	                } else {
	                    System.out.println("Connection kept alive...");
	                }
	            }
	        } finally {
	            conn.close();
	        }
	    }
}
