if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  echo -e "Starting to update btravers.github.io\n"

  cp backend/target/*.war $HOME/

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis"
  git clone https://${GH_TOKEN}@github.com/btravers/btravers.github.io.git

  cd btravers.github.io
  cp $HOME/*.war .

  git add *.war
  git commit -am "Travis build $TRAVIS_BUILD_NUMBER pushed to btravers.github.io"
  git push origin master

  echo -e "Done\n"
fi
