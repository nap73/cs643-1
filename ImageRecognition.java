package com.amazonaws.samples;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.sqs.*;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import java.util.List;
 
public class ImageRecognition {
	public static void main(String[] args) throws Exception {

 	   
		String url = "https://sqs.us-east-1.amazonaws.com/44585959/MyQueue" ;
				  BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
				  ses_input.getAccessKeyId(), ses_input.getSecretAccessKey(),
				  ses_input.getSessionToken());
		 
				  final AmazonSQS sqs = AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(sessionCredentials)).withRegion("us-east-1").build();

	      
	      String bucket = "njit-cs-643";
	      String ResImage = "";
	      AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
	    		  .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
	    		  .withRegion(Regions.US_EAST_1)
	              .build();
	      
	      
	      final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
       
	      ListObjectsV2Result result1 = s3.listObjectsV2(bucket);
	      List<S3ObjectSummary> objects = result1.getObjectSummaries();
	      
	      
	        System.out.println("---------------------------------------------------------");
	        System.out.println("IMAGE RECOGNITION RESULT ");
	        System.out.println("---------------------------------------------------------");
	      
	      System.out.println("Images of CAR in the bucket :");
		  for (S3ObjectSummary os : objects) {
			  DetectLabelsRequest request = new DetectLabelsRequest()
                      .withImage(new Image()
                      .withS3Object(new S3Object()
                      .withName(os.getKey()).withBucket(bucket)))
                      .withMaxLabels(10)
                      .withMinConfidence(90F);

	    	  
		   //   ResImage=ResImage.concat(os.getKey()+",");
	             
			  
	    	  try {
	    		  
	    		  
	    	         DetectLabelsResult result = rekognitionClient.detectLabels(request);
		 	         List <Label> labels = result.getLabels();
		 	       	for (Label label: labels) {
                    if(label.getName().equalsIgnoreCase("car"))  { 	
                       System.out.println(os.getKey() + "  Confidence : " +label.getConfidence() );    
		 	       
                       ResImage=ResImage.concat(os.getKey()+",");
		 	        }
		 	       	}
	    	  }
		 	       	
		 	       	
		 	       catch(Exception ee) {
			    	  }
		 	       	
		 	       	
	    	//  CreateQueueRequest createQueueRequest = new CreateQueueRequest("MyQueue");
	      //    String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();     	
	    //	  System.out.println(myQueueUrl);
	    	  
	    	  
		 	/*     ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		           try {
		               credentialsProvider.getCredentials();
		           } catch (Exception e) {
		               throw new AmazonClientException(
		                       "Cannot load the credentials from the credential profiles file. " +
		                       "Please make sure that your credentials file is at the correct " +
		                       "location (/Users/neel/.aws/credentials), and is in valid format.",
		                       e);
		           }
		 	   
				*/		System.out.println();
					//	System.out.println(ResImage);
							
       		        sqs.sendMessage(new SendMessageRequest(url,ResImage));
       		        
                    }
	   sqs.sendMessage(new SendMessageRequest(url,ResImage));
	//	   System.out.println(ResImage);
	   System.out.println("Image Keys successfully send for text recognition");
                    
	}

}

	    	  
	    	
