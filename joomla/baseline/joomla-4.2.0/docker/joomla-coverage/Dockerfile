FROM olianasd/joomla4stile

ENV COMPOSER_ALLOW_SUPERUSER=1
WORKDIR /coverage-tools

RUN composer init --no-interaction --name=coverage/tools \
    ; composer require --no-interaction --prefer-dist --no-progress --no-scripts --dev \
        phpunit/php-code-coverage \
        phpunit/phpcov
