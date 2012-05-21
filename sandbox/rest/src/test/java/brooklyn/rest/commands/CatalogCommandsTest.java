package brooklyn.rest.commands;

import brooklyn.rest.commands.catalog.ListCatalogEntitiesCommand;
import brooklyn.rest.commands.catalog.ListCatalogPoliciesCommand;
import brooklyn.rest.commands.catalog.ListConfigKeysCommand;
import brooklyn.rest.commands.catalog.LoadClassCommand;
import brooklyn.rest.resources.CatalogResource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import org.testng.annotations.Test;

public class CatalogCommandsTest extends BrooklynCommandTest {

  @Override
  protected void setUpResources() throws Exception {
    addResource(new CatalogResource());
  }

  @Test
  public void testListCatalogEntities() throws Exception {
    runCommandWithArgs(ListCatalogEntitiesCommand.class);

    assertThat(standardOut(), containsString("brooklyn.entity.webapp.jboss.JBoss6Server"));
  }

  @Test
  public void testListCatalogPolicies() throws Exception {
    runCommandWithArgs(ListCatalogPoliciesCommand.class);

    assertThat(standardOut(), containsString("brooklyn.policy.followthesun.FollowTheSunPolicy"));
  }

  @Test
  public void testListConfigKeysForEntity() throws Exception {
    runCommandWithArgs(ListConfigKeysCommand.class, "brooklyn.entity.nosql.redis.RedisStore");

    assertThat(standardOut(), containsString("redis.port"));
  }

  @Test
  public void testMissingConfigKeyNameForEntity() throws Exception {
    runCommandWithArgs(ListConfigKeysCommand.class);

    assertThat(standardErr(), containsString("The type of the entity is mandatory"));
  }

  @Test
  public void testEntityTypeNotFound() throws Exception {
    runCommandWithArgs(ListConfigKeysCommand.class, "dummy-entity-name");

    assertThat(standardErr(), containsString("Client response status: 404"));
  }

  @Test
  public void testUploadGroovyScriptToCreateEntity() throws Exception {
    String groovyScript = "package brooklyn.rest.entities.cli.custom\n" +
        "" +
        "import brooklyn.entity.basic.AbstractEntity\n" +
        "import brooklyn.entity.Entity\n" +
        "import brooklyn.event.basic.BasicConfigKey\n" +
        "" +
        "class DummyEntity extends AbstractEntity {\n" +
        "  public static final BasicConfigKey<String> DUMMY_CFG = [ String, \"dummy.config\", \"Dummy Config\" ]\n" +
        "  public DummyEntity(Map properties=[:], Entity owner=null) {\n" +
        "        super(properties, owner)" +
        "  }" +
        "}\n";

    File temp = createTemporaryFileWithContent("brooklyn-rest", ".groovy", groovyScript);
    runCommandWithArgs(LoadClassCommand.class, temp.getAbsolutePath());

    assertThat(standardOut(), containsString("http://localhost:8080/v1/catalog/entities/brooklyn.rest.entities.cli.custom.DummyEntity"));
  }

  public File createTemporaryFileWithContent(String prefix, String suffix, String content)
      throws IOException {
    File temporaryFile = File.createTempFile(prefix, suffix);
    Writer writer = null;

    try {
      writer = new FileWriter(temporaryFile);
      writer.write(content);
      return temporaryFile;

    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }
}
