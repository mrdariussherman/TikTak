# Author: Darius Sherman
# Assignment: Program 2
# Class: CSI 4321

javac tiktak/topic.serialization/*.java
javac tiktak/app/client/*.java

# Valid TOST
echo "Valid TOST Test\n----------------"
java -cp . tiktak.app.client.Client wind.ecs.baylor.edu 12345 sherman vzlzb TOST

# Valid LTSRL
echo "Valid LTSRL Test\n----------------"
java -cp . tiktak.app.client.Client wind.ecs.baylor.edu 12345 sherman vzlzb LTSRL Baylor ./BU.png

# Too few TOST arguments
echo "Invalid TOST Test - Missing Arguments\n----------------"
java -cp . tiktak.app.client.Client wind.ecs.baylor.edu 12345 TOST

# Too few LTSRL arguments
echo "Invalid LTSRL Test - Missing Arguments\n----------------"
java -cp . tiktak.app.client.Client wind.ecs.baylor.edu 12345 sherman vzlzb LTSRL

# Too many TOST arguments
echo "Invalid TOST Test - Too Many Arguments\n----------------"
java -cp . tiktak.app.client.Client wind.ecs.baylor.edu 12345 sherman vzlzb TOST TOO MANY Args

# Too many LTSRL arguments
echo "Invalid LTSRL Test - Too Many Arguments\n----------------"
java -cp . tiktak.app.client.Client wind.ecs.baylor.edu 12345 sherman vzlzb LTSRL TOO MANY Args

# Invalid LTSRL credentials
echo "Invalid LTSRL Test - Invalid Credentials\n----------------"
java -cp . tiktak.app.client.Client wind.ecs.baylor.edu 12345 darius hello LTSRL Baylor ./BU.png

# Invalid TOST credentials
echo "Invalid TOST Test - Invalid Credentials\n----------------"
java -cp . tiktak.app.client.Client wind.ecs.baylor.edu 12345 darius hello TOST

# Invalid LTSRL no image
echo "Invalid LTSRL Test - No image provided\n----------------"
java -cp . tiktak.app.client.Client wind.ecs.baylor.edu 12345 sherman vzlzb LTSRL Baylor

# Invalid LTSRL invalid file
echo "Invalid LTSRL Test - File does not exist\n----------------"
java -cp . tiktak.app.client.Client wind.ecs.baylor.edu 12345 sherman vzlzb LTSRL Baylor ./file.png