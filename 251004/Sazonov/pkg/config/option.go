package configutil

import (
	"strings"

	"github.com/spf13/viper"
)

type (
	option interface {
		Apply(cfg *viper.Viper)
	}

	optionFunc func(cfg *viper.Viper)
)

func (of optionFunc) Apply(cfg *viper.Viper) {
	of(cfg)
}

func WithConfigPaths(paths []string) optionFunc {
	return func(cfg *viper.Viper) {
		for _, path := range paths {
			cfg.AddConfigPath(path)
		}
	}
}

func WithConfigType(cfgType string) optionFunc {
	return func(cfg *viper.Viper) {
		cfg.SetConfigType(cfgType)
	}
}

func WithConfigName(name string) optionFunc {
	return func(cfg *viper.Viper) {
		cfg.SetConfigName(name)
	}
}

func WithEnvs(envPrefix string) optionFunc {
	return func(cfg *viper.Viper) {
		cfg.SetEnvPrefix(envPrefix)
		cfg.SetEnvKeyReplacer(strings.NewReplacer(".", "_"))
		cfg.AutomaticEnv()
	}
}

func WithDefaults(defaults map[string]any) optionFunc {
	return func(cfg *viper.Viper) {
		if defaults == nil {
			return
		}

		for k, v := range defaults {
			cfg.SetDefault(k, v)
		}
	}
}
