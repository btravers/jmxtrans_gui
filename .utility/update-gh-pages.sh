if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  echo -e "Starting to update gh-pages\n"

  mkdir $HOME/gh-pages
  cp backend/target/*.war $HOME/gh-pages

  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis"

  cd $HOME/gh-pages
  git init
  git remote add origin https://${GH_TOKEN}@github.com/btravers/jmxtrans_gui.git > /dev/null
  git checkout -B gh-pages

  git add .
  git commit -am "Travis build $TRAVIS_BUILD_NUMBER pushed to master"
  git push origin gh-pages -fq > /dev/null

  echo -e "Done\n"
fi
