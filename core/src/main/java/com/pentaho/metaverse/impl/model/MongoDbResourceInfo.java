package com.pentaho.metaverse.impl.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pentaho.metaverse.api.model.BaseResourceInfo;
import com.pentaho.metaverse.api.model.IExternalResourceInfo;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.trans.steps.mongodb.MongoDbMeta;

/**
 * User: RFellows Date: 3/10/15
 */
public class MongoDbResourceInfo extends BaseResourceInfo implements IExternalResourceInfo {

  public static final String JSON_PROPERTY_PORT = "port";
  public static final String JSON_PROPERTY_USERNAME = "username";
  public static final String JSON_PROPERTY_PASSWORD = "password";
  public static final String JSON_PROPERTY_DATABASE_NAME = "databaseName";
  public static final String JSON_PROPERTY_CONNECTION_TIMEOUT = "connectionTimeout";
  public static final String JSON_PROPERTY_HOST_NAMES = "hostNames";
  public static final String JSON_PROPERTY_SOCKET_TIMEOUT = "socketTimeout";
  public static final String JSON_PROPERTY_USE_ALL_REPLICA_SET_MEMBERS = "useAllReplicaSetMembers";
  public static final String JSON_PROPERTY_USE_KERBEROS_AUTHENTICATION = "useKerberosAuthentication";

  private String database;
  private String port;
  private String hostNames;
  private String user;
  private String password;
  private boolean useAllReplicaSetMembers;
  private boolean useKerberosAuthentication;
  private String connectTimeout;
  private String socketTimeout;

  public MongoDbResourceInfo( MongoDbMeta mongoDbMeta ) {
    setDatabase( mongoDbMeta.getDbName() );
    setPort( mongoDbMeta.getPort() );
    setHostNames( mongoDbMeta.getHostnames() );
    setUser( mongoDbMeta.getAuthenticationUser() );
    setPassword( mongoDbMeta.getAuthenticationPassword() );
    setUseAllReplicaSetMembers( mongoDbMeta.getUseAllReplicaSetMembers() );
    setUseKerberosAuthentication( mongoDbMeta.getUseKerberosAuthentication() );
    setConnectTimeout( mongoDbMeta.getConnectTimeout() );
    setSocketTimeout( mongoDbMeta.getSocketTimeout() );
  }

  public MongoDbResourceInfo( String hostNames, String port, String database ) {
    setHostNames( hostNames );
    setPort( port );
    setDatabase( database );
  }

  @JsonProperty( JSON_PROPERTY_CONNECTION_TIMEOUT )
  public String getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout( String connectTimeout ) {
    this.connectTimeout = connectTimeout;
  }

  @JsonProperty( JSON_PROPERTY_DATABASE_NAME )
  public String getDatabase() {
    return database;
  }

  public void setDatabase( String database ) {
    this.database = database;
  }

  @JsonProperty( JSON_PROPERTY_HOST_NAMES )
  public String getHostNames() {
    return hostNames;
  }

  public void setHostNames( String hostNames ) {
    this.hostNames = hostNames;
  }

  @JsonProperty( JSON_PROPERTY_PORT )
  public String getPort() {
    return port;
  }

  public void setPort( String port ) {
    this.port = port;
  }

  @JsonProperty( JSON_PROPERTY_SOCKET_TIMEOUT )
  public String getSocketTimeout() {
    return socketTimeout;
  }

  public void setSocketTimeout( String socketTimeout ) {
    this.socketTimeout = socketTimeout;
  }

  @JsonProperty( JSON_PROPERTY_USE_ALL_REPLICA_SET_MEMBERS )
  public boolean isUseAllReplicaSetMembers() {
    return useAllReplicaSetMembers;
  }

  public void setUseAllReplicaSetMembers( boolean useAllReplicaSetMembers ) {
    this.useAllReplicaSetMembers = useAllReplicaSetMembers;
  }

  @JsonProperty( JSON_PROPERTY_USE_KERBEROS_AUTHENTICATION )
  public boolean isUseKerberosAuthentication() {
    return useKerberosAuthentication;
  }

  public void setUseKerberosAuthentication( boolean useKerberosAuthentication ) {
    this.useKerberosAuthentication = useKerberosAuthentication;
  }

  @JsonProperty( JSON_PROPERTY_USERNAME )
  public String getUser() {
    return user;
  }

  public void setUser( String user ) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  @JsonProperty( JSON_PROPERTY_PASSWORD )
  public void setPassword( String password ) {
    this.password = Encr.decryptPasswordOptionallyEncrypted( password );
  }

  @JsonProperty( JSON_PROPERTY_PASSWORD )
  protected String getEncryptedPassword() {
    // Need "Encrypted prefix for decryptPasswordOptionallyEncrypted() to operate properly
    return Encr.PASSWORD_ENCRYPTED_PREFIX + Encr.encryptPassword( password );
  }
}
