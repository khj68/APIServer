package com.insrb.app.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
public class HouseControllerTest {

	private static final String ACCESS_KEY = "myValue";

	@Autowired
	private MockMvc mockMvc;

	Map<String, String> mockAddress;

	{
		mockAddress = new HashMap<String, String>();
		mockAddress.put("search_text", "효창동 현대아트빌");
	}

	@Test
	@DisplayName("UI-APP-020 주소찾기 :  POST /users 파라미터가 없으면 400 BAD_REQUEST를 리턴해야한다")
	public void UIAPP020_01() throws Exception {
		mockMvc.perform(get("/house/juso").header("X-insr-servicekey", ACCESS_KEY)).andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("UI-APP-020 주소찾기 :  POST /users 파라미터가 없으면 400 BAD_REQUEST를 리턴해야한다")
	public void UIAPP020_02() throws Exception {
		mockMvc
			.perform(get("/house/juso").header("X-insr-servicekey", ACCESS_KEY).param("search", mockAddress.get("search_text")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.results.common.errorCode").value("0"));
	}
}
