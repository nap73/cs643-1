package com.amazonaws.samples;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

public class TextRecognotion {

	
	
	  static BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
			  ses_input.getAccessKeyId(), ses_input.getSecretAccessKey(),
			  ses_input.getSessionToken());
	
	 static String bucket = "njit-cs-643";
	public static void main(String a[]) throws IOException {
	
	
		
		/*
	  
  ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
    try {
        credentialsProvider.getCredentials();
    } catch (Exception e) {
        throw new AmazonClientException(
                "Cannot load the credentials from the credential profiles file. " +
                "Please make sure that your credentials file is at the correct " +
                "location (/Users/neel/.aws/credentials), and is in valid format.",
                e);
    }
	
	*/
		
		
		

		   AmazonSQS sqs = AmazonSQSClientBuilder.standard()
	             .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
	             .withRegion(Regions.US_EAST_1)
	             .build();



		System.out.println("===========================================");
		System.out.println("Receiving with Amazon SQS");
		System.out.println("===========================================\n");

	    // Receive messages
        System.out.println("Receiving messages from MyQueue.\n");
        String url = "https://sqs.us-east-1.amazonaws.com/4488882959/MyQueue";
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(url);
        
        String result = "";
        
        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        System.out.println();
        for (Message message : messages) {
            System.out.println("  Message Received for Text Recognition");
            System.out.println("    MessageId:     " + message.getMessageId());
            System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
            System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
            System.out.println("    Body:          " + message.getBody());
            result = message.getBody();
            for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                System.out.println("  Attribute");
                System.out.println("    Name:  " + entry.getKey());
                System.out.println("    Value: " + entry.getValue());
            }
        }
        
        
        System.out.println("---------------------------------------------------------");
        System.out.println("TEXT DETECTION RESULT");
        System.out.println("---------------------------------------------------------");
             
    	ArrayList<String>  Images = new ArrayList<String>();
    
    	  FileWriter write = new FileWriter("result.txt") ;
    	  BufferedWriter bw = new BufferedWriter(write);
 		 
        char ch = '\0';
		String temp="";

    	 for (int i = 0; i < result.length(); i++) {

			ch = result.charAt(i);
			

			if(ch != ',') {
				temp = temp.concat(Character.toString(ch));

			}else {
				Images.add(temp);
				temp="";
			}
        	 }
   	 
   	  
   	  AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
   		  .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
   		  .withRegion(Regions.US_EAST_1)
             .build();
     
    	 
    	 
    	 for(int j=0 ; j < Images.size() ; j++)
    	 {
    		 System.out.println(Images.get(j));
    
    		 DetectTextRequest requestText = new DetectTextRequest()
    	                .withImage(new Image()
    	                .withS3Object(new S3Object()
    	                .withName(Images.get(j))
    	                .withBucket(bucket)));
    	      

    	        try {
    	           DetectTextResult resultText = rekognitionClient.detectText(requestText);
    	           List<TextDetection> textDetections = resultText.getTextDetections();
    	           if(textDetections.size()!=0) {
    	        	   System.out.println("Detected Text for " +Images.get(j) );
    	        	   bw.write("Detected Text for " +Images.get(j) );
    	        	   bw.newLine();  
    	        	   bw.newLine();  
    	     	        
    	        	   
    	           }
    	          for (TextDetection text: textDetections) {
    	        
    	                   System.out.println(" Text : " + text.getDetectedText());
    	                  bw.write("Text :  " +text.getDetectedText() );
    	                  bw.newLine();  
    	          }
    	   //       bw.write("--------------------------------");
    	          bw.newLine();  
     	         
      	        
    	          
    	        } catch(AmazonRekognitionException e) {
    	           e.printStackTrace();
    	        }
    	        
    	//        System.out.println("---------------------------------------------------------");
    	        
    	       }
    	 bw.close();  
         System.out.println("---------------------------------------------------------");
         System.out.println("writing output file completed");
         
    	 
    	 }
	}

