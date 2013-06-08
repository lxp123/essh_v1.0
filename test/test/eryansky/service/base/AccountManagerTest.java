package test.eryansky.service.base;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Maps;
import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.User;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.jdbc.JdbcDao;
import com.eryansky.common.utils.io.PropertiesLoader;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.entity.base.Menu;
import com.eryansky.entity.base.Role;
import com.eryansky.service.CommonManager;
import com.eryansky.service.base.MenuManager;
import com.eryansky.service.base.RoleManager;
/**
 * Account单元测试
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2012-8-17 上午9:31:38
 */
public class AccountManagerTest {
	
	@SuppressWarnings("unused")
    private static Properties pro;
	
	private static JdbcDao jdbcDao;
	private static MenuManager menuManager;
	private static CommonManager commonManager;
	private static RoleManager roleManager;
	
	
	@BeforeClass
	public static void init() throws Exception{
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-jdbc.xml");
		jdbcDao = (JdbcDao)context.getBean("jdbcDao");
		menuManager = (MenuManager)context.getBean("menuManager");
		commonManager = (CommonManager)context.getBean("commonManager");
		roleManager = (RoleManager)context.getBean("roleManager");
		
		pro = new PropertiesLoader("/appconfig.properties").getProperties();
	}
	
	@Test
    public void test2(){
	    Map<String, String> map = Maps.newHashMap();
	    map.put("name", "%");
	    System.out.println(jdbcDao.findForListMap("select * from t_base_menu where name like :name ",  map));
    }
	
	
	@Test
    public void page(){
		String sql = "select * from T_BASE_USER";
		List list1 = jdbcDao.findForJdbc(sql, 1, 20);
		System.out.println(JsonMapper.nonEmptyMapper().toJson(list1));
    }
	
	@Test
    public void assertt(){
		System.out.println(commonManager.getIdByProperty("Menu", "name", "菜单管理"));
		System.out.println(commonManager.getIdByTFO("T_BASE_MENU", "NAME", "菜 单管理"));
    }
	@Test
    public void validator(){
		try {
			menuManager.save(new Menu());
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			javax.validation.ConstraintViolationException ce = (javax.validation.ConstraintViolationException) e;
			Set<ConstraintViolation<?>> set =  ce.getConstraintViolations();
			Iterator<?> iterator = set.iterator();
			int i = -1;
			while(iterator.hasNext()){
				ConstraintViolation<?> c = (ConstraintViolation<?>) iterator.next();
				sb.append(c.getMessage());
				i++;
				if (i < set.size() - 1) {
					sb.append(",");
				}else{
					sb.append(".");
				}
			}
			System.out.println(sb.toString());
			e.printStackTrace();
		}
	}
	
	@Test
    public void bf(){
		Thread thread1 = new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<1000;i++){
					try {
						Menu m = new Menu();
						m.setName(i+"");
						menuManager.save(m);
						System.out.println(1+ " "+i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		Thread thread2 = new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<1000;i++){
					try {
						Role r = new Role();
						r.setName(i+"");
						roleManager.save(r);
						System.out.println(2+ " "+i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread1.start();
		thread2.start();
    }
	

}

