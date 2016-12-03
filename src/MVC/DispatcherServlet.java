package MVC;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.reflections.Reflections;



@SuppressWarnings("serial")
public class DispatcherServlet extends HttpServlet {
	
	private Map<String, Handler> handleMap;
	private List<Object> contollers;
	
	@Override  
    public void init() throws ServletException {
		handleMap = new HashMap<>();
		contollers = new ArrayList<>();
		loadControllers();
		
	}
	
	private void loadControllers(){
		Reflections reflections = new Reflections("test");
		
		Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(Controller.class);
		for (Class<?> controllerClass : controllerClasses) {
			try {
				contollers.add(controllerClass.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI().toString();
		int begin_index = uri.lastIndexOf('/');
		if(begin_index != -1){
			String param = uri.substring(begin_index);
			Handler handler = (Handler)handleMap.get(param);
			if(handler == null){
				if(this.setHandler(param)){
					handler = handleMap.get(param);
				}
				else{
					
					super.service(req, resp);
				}
			}
			try {
				ModelAndView modelAndView = handler.handle(new ModelAndView(req));
				String jspName = modelAndView.getViewName();
				if(jspName != null){
					Map<String, Object> paramMap = modelAndView.getKeyMap();
					for(Map.Entry<String, Object> entry : paramMap.entrySet()){
						req.setAttribute(entry.getKey(), entry.getValue());
					}
					req.getRequestDispatcher(jspName + ".jsp").forward(req, resp);
				}
			} catch (Exception e) {
				
				e.printStackTrace();
				resp.setStatus(500);
			}
			
		}else{
			super.service(req, resp);
		}
	}
	
	private void addHandler(String name, Handler handler){
		handleMap.put(name, handler);
	}
	
	private boolean setHandler(String param){
		boolean found = false;
		Handler handler = new Handler();
		RequestMapping requestMapping = null;
		for (Object object : contollers) {
			Method[] methods = object.getClass().getDeclaredMethods();
			for (Method method : methods) {
				requestMapping = method.getAnnotation(RequestMapping.class);
				if(requestMapping != null && requestMapping.value().equals(param)){
					found = true;
					handler.setHandleObj(object);
					handler.setMethod(method);
					addHandler(param, handler);
					break;
				}
			}
			if(found)	break;
		}
		
		return found;
	}
	
	
	
}
