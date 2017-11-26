
# Sensei - IoT without programming

## Build and usage instructions:

Start up the Orange Pi with the CR-tech SRM platform and using the SRMScriptEditor, save the `config_no_comments.txt` initialization file to the device.

Assuming an Arch Linux installation, the following packages are required
* apache
* php (ver. 7)
* php-apache
* mariadb
* [yii framework](http://www.yiiframework.com/) (Version 1.1)

Configure the packages according to the Arch wiki documentation.

Clone the repository to the `/srv` directory.

Provide the Yii framework Yii at the `/srv/framework` - do note that if you use the package available on the AUR repository, you can do a symlink `framework -> /usr/share/webapps/yii/`

Apply the `bajtahack\db.sql` script to the database for the initial sensor configuration - currently the database is set for group N SRM-modules.

At this point the service should be available at `http://localhost/index.php`

---

Have *fun* with using our platform!
