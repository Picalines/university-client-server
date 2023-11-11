package com.practice4;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import com.practice4.*;

@Controller
public class EchoController {
	@MessageMapping("/echo")
	@SendTo("/topic/echoes")
	public EchoMessage echo(EchoMessage message) throws Exception {
		Thread.sleep(1000);
		return new EchoMessage(HtmlUtils.htmlEscape(message.getMessage()));
	}
}
