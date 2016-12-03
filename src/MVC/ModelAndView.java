package MVC;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ModelAndView {

	private String viewName;
	private Map<String, Object> requestMap = new HashMap<>();
	private Map<String, Object> keyMap = new HashMap<>();

	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	public Object getMap(String name) {
		return  requestMap.get(name);
	}
	public void setMap(Map<String, Object> requestMap) {
		this.requestMap = requestMap;
	}
	
	public ModelAndView addObject(String name, Object value) {
		keyMap.put(name, value);
		return this;
	}
	public Map<String, Object> getKeyMap() {
		return keyMap;
	}
	public void setKeyMap(Map<String, Object> keyMap) {
		this.keyMap = keyMap;
	}

	public ModelAndView(HttpServletRequest req) {
		Enumeration<String> paramNames = req.getParameterNames();  
        while (paramNames.hasMoreElements()) {  
            String paramName = paramNames.nextElement();  
  
            String[] paramValues = req.getParameterValues(paramName);  
            if (paramValues.length == 1) {  
                String paramValue = paramValues[0];  
                if (paramValue.length() != 0) {  
                    requestMap.put(paramName, paramValue);  
                }  
            }  
        }  
	}
}
