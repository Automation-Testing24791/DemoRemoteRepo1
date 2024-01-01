package GSheetHandler;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class GSheetUtil
{
   private static Sheets sheetsService;
   private static String APPLICATION_NAME = "Google Sheets Example";
   private static String SPREADSHEET_ID = "1EWC9jFxZ9dS0NdCJZkcORmi9J2pOTMfrHOzYX3eYRYw";


   private static Credential authorize() throws IOException, GeneralSecurityException
   {
      InputStream is = GSheetUtil.class.getResourceAsStream("/credentials.json");
      GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
              JacksonFactory.getDefaultInstance(), new InputStreamReader(is));

      List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
              GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
              clientSecrets, scopes)
              .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
              .setAccessType("offline")
              .build();

      Credential credential = new AuthorizationCodeInstalledApp(
              flow, new LocalServerReceiver())
              .authorize("user");


      return credential;
   }


   public static Sheets getSheetsService() throws GeneralSecurityException, IOException
   {
      Credential credential = authorize();
      return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
              JacksonFactory.getDefaultInstance(), credential)
              .setApplicationName(APPLICATION_NAME)
              .build();
   }


   public static void main(String[] args) throws GeneralSecurityException, IOException
   {
      sheetsService = getSheetsService();
      String range = "Congress";

      ValueRange response = sheetsService.spreadsheets().values()
              .get(SPREADSHEET_ID, range)
              .execute();

      List<List<Object>> values = response.getValues();

      if(values==null || values.size()<=1)
      {
         System.out.println("No data found....");
      }
      else
      {
          for (int i = 1; i < values.size(); i++)
          {
              List<Object> row = values.get(i);
              for (Object cell : row)
              {
                  System.out.print(cell + " ");
              }
              System.out.println(); // Move to the next line after printing a row
          }
      }
   }
}
