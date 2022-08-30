package com.snms.controllers;



import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class CaptchaController {
	private static final Logger logger = Logger.getLogger(CaptchaController.class);
	
	@Autowired
	ServletContext context;
	
	
	@Autowired 
	 private HttpSession httpSession;
	
	@Autowired
	private HttpServletRequest request;
	 private static String randString = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	    public String generateCaptcha(String path)
	            throws IOException {
	    	
	    	
	    	String UPLOADED_FOLDER = path+"/images/myCaptcha/captcha.png";
	    	
	    	 OutputStream output=new FileOutputStream(UPLOADED_FOLDER);
	        // 
	        int width = 120, height = 40;
	        BufferedImage image = new BufferedImage(width, height,
	                BufferedImage.TYPE_INT_RGB);
	        // 
	        Graphics g = image.getGraphics();
	        // 
	        Random random = new Random();
	        // 
	        g.setColor(getRandColor(230, 250));
	        g.fillRect(0, 0, width, height);
	        // 
	        g.setFont(new Font("Times New Roman", Font.PLAIN, 26));
	        // 155
	        g.setColor(getRandColor(1, 245));
	        for (int i = 0; i < 2; i++) {
	            int x = random.nextInt(width);
	            int y = random.nextInt(height);
	            int xl = random.nextInt(12);
	            int yl = random.nextInt(12);
	            g.drawLine(x, y, x + xl, y + yl);
	        }
	        // ()
	        String randomString = "";
	        for (int i = 0; i < 9; i++) {
	            String rand = String.valueOf(randString.charAt(random
	                    .nextInt(randString.length())));
	            randomString += rand;
	            // 
	            g.setColor(new Color(random.nextInt(101), random.nextInt(111),
	                    random.nextInt(121)));
	            // 
	            g.drawString(rand, 13 * i + 10, 24);
	           
	        }
	       
	        System.err.println(randomString+"jjjjj");
	        
	        RequestAttributes requestAttributes = RequestContextHolder
	                .currentRequestAttributes();
	        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
	        HttpServletRequest request = attributes.getRequest();
	        HttpSession httpSession = request.getSession(true);
			  
			  
			
			 httpSession.setAttribute("captcha",randomString);
			 
			 
	        g.dispose();
	        //   tomcattemp
	        try {
	            ImageIO.write(image, "PNG", output);
	        } catch (IOException e) {
	            throw new IOException(e);
	        }
	        finally {
				if (output != null) {
				safeClose(output);
				}
			}
	        return randomString;
	    }

	    private void safeClose(OutputStream output) {
	    	if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					logger.info(e.getMessage());
				}
				}
			
		}

		private static Color getRandColor(int fc, int bc) {
	        Random random = new Random();
	        if (fc > 255)
	            fc = 255;
	        if (bc > 255)
	            bc = 255;
	        int r = fc + random.nextInt(bc - fc);
	        int g = fc + random.nextInt(bc - fc);
	        int b = fc + random.nextInt(bc - fc);
	        return new Color(r, g, b);
	    }
	    
	    @RequestMapping(value = "/refreshCaptcha", method = RequestMethod.POST)
		public ResponseEntity<String> refreshCaptcha(HttpServletRequest request) throws Exception {
	    	String path = context.getRealPath("/");
	    	 generateCaptcha(path);
			return ResponseEntity.ok("Success");

		}
	    
	   
}




